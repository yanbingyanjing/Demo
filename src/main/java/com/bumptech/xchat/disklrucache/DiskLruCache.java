//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bumptech.xchat.disklrucache;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class DiskLruCache implements Closeable {
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_TEMP = "journal.tmp";
    static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    static final String MAGIC = "libcore.io.DiskLruCache";
    static final String VERSION_1 = "1";
    static final long ANY_SEQUENCE_NUMBER = -1L;
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    private static final String REMOVE = "REMOVE";
    private static final String READ = "READ";
    private final File directory;
    private final File journalFile;
    private final File journalFileTmp;
    private final File journalFileBackup;
    private final int appVersion;
    private long maxSize;
    private final int valueCount;
    private long size = 0L;
    private Writer journalWriter;
    private final LinkedHashMap<String, DiskLruCache.Entry> lruEntries = new LinkedHashMap(0, 0.75F, true);
    private int redundantOpCount;
    private long nextSequenceNumber = 0L;
    final ThreadPoolExecutor executorService;
    private final Callable<Void> cleanupCallable;

    private DiskLruCache(File directory, int appVersion, int valueCount, long maxSize) {
        this.executorService = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        this.cleanupCallable = new Callable<Void>() {
            public Void call() throws Exception {
                DiskLruCache var1 = DiskLruCache.this;
                synchronized(DiskLruCache.this) {
                    if (DiskLruCache.this.journalWriter == null) {
                        return null;
                    } else {
                        DiskLruCache.this.trimToSize();
                        if (DiskLruCache.this.journalRebuildRequired()) {
                            DiskLruCache.this.rebuildJournal();
                            DiskLruCache.this.redundantOpCount = 0;
                        }

                        return null;
                    }
                }
            }
        };
        this.directory = directory;
        this.appVersion = appVersion;
        this.journalFile = new File(directory, "journal");
        this.journalFileTmp = new File(directory, "journal.tmp");
        this.journalFileBackup = new File(directory, "journal.bkp");
        this.valueCount = valueCount;
        this.maxSize = maxSize;
    }

    public static DiskLruCache open(File directory, int appVersion, int valueCount, long maxSize) throws IOException {
        if (maxSize <= 0L) {
            throw new IllegalArgumentException("maxSize <= 0");
        } else if (valueCount <= 0) {
            throw new IllegalArgumentException("valueCount <= 0");
        } else {
            File backupFile = new File(directory, "journal.bkp");
            if (backupFile.exists()) {
                File journalFile = new File(directory, "journal");
                if (journalFile.exists()) {
                    backupFile.delete();
                } else {
                    renameTo(backupFile, journalFile, false);
                }
            }

            DiskLruCache cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
            if (cache.journalFile.exists()) {
                try {
                    cache.readJournal();
                    cache.processJournal();
                    return cache;
                } catch (IOException var8) {
                    System.out.println("DiskLruCache " + directory + " is corrupt: " + var8.getMessage() + ", removing");
                    cache.delete();
                }
            }

            directory.mkdirs();
            cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
            cache.rebuildJournal();
            return cache;
        }
    }

    private void readJournal() throws IOException {
        StrictLineReader reader = new StrictLineReader(new FileInputStream(this.journalFile), Util.US_ASCII);

        try {
            String magic = reader.readLine();
            String version = reader.readLine();
            String appVersionString = reader.readLine();
            String valueCountString = reader.readLine();
            String blank = reader.readLine();
            if ("libcore.io.DiskLruCache".equals(magic) && "1".equals(version) && Integer.toString(this.appVersion).equals(appVersionString) && Integer.toString(this.valueCount).equals(valueCountString) && "".equals(blank)) {
                int lineCount = 0;

                while(true) {
                    try {
                        this.readJournalLine(reader.readLine());
                        ++lineCount;
                    } catch (EOFException var12) {
                        this.redundantOpCount = lineCount - this.lruEntries.size();
                        if (reader.hasUnterminatedLine()) {
                            this.rebuildJournal();
                            return;
                        }

                        this.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFile, true), Util.US_ASCII));
                        return;
                    }
                }
            } else {
                throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + "]");
            }
        } finally {
            Util.closeQuietly(reader);
        }
    }

    private void readJournalLine(String line) throws IOException {
        int firstSpace = line.indexOf(32);
        if (firstSpace == -1) {
            throw new IOException("unexpected journal line: " + line);
        } else {
            int keyBegin = firstSpace + 1;
            int secondSpace = line.indexOf(32, keyBegin);
            String key;
            if (secondSpace == -1) {
                key = line.substring(keyBegin);
                if (firstSpace == "REMOVE".length() && line.startsWith("REMOVE")) {
                    this.lruEntries.remove(key);
                    return;
                }
            } else {
                key = line.substring(keyBegin, secondSpace);
            }

            DiskLruCache.Entry entry = (DiskLruCache.Entry)this.lruEntries.get(key);
            if (entry == null) {
                entry = new DiskLruCache.Entry(key);
                this.lruEntries.put(key, entry);
            }

            if (secondSpace != -1 && firstSpace == "CLEAN".length() && line.startsWith("CLEAN")) {
                String[] parts = line.substring(secondSpace + 1).split(" ");
                entry.readable = true;
                entry.currentEditor = null;
                entry.setLengths(parts);
            } else if (secondSpace == -1 && firstSpace == "DIRTY".length() && line.startsWith("DIRTY")) {
                entry.currentEditor = new DiskLruCache.Editor(entry);
            } else if (secondSpace != -1 || firstSpace != "READ".length() || !line.startsWith("READ")) {
                throw new IOException("unexpected journal line: " + line);
            }

        }
    }

    private void processJournal() throws IOException {
        deleteIfExists(this.journalFileTmp);
        Iterator i = this.lruEntries.values().iterator();

        while(true) {
            while(i.hasNext()) {
                DiskLruCache.Entry entry = (DiskLruCache.Entry)i.next();
                int t;
                if (entry.currentEditor == null) {
                    for(t = 0; t < this.valueCount; ++t) {
                        this.size += entry.lengths[t];
                    }
                } else {
                    entry.currentEditor = null;

                    for(t = 0; t < this.valueCount; ++t) {
                        deleteIfExists(entry.getCleanFile(t));
                        deleteIfExists(entry.getDirtyFile(t));
                    }

                    i.remove();
                }
            }

            return;
        }
    }

    private synchronized void rebuildJournal() throws IOException {
        if (this.journalWriter != null) {
            this.journalWriter.close();
        }

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFileTmp), Util.US_ASCII));

        try {
            writer.write("libcore.io.DiskLruCache");
            writer.write("\n");
            writer.write("1");
            writer.write("\n");
            writer.write(Integer.toString(this.appVersion));
            writer.write("\n");
            writer.write(Integer.toString(this.valueCount));
            writer.write("\n");
            writer.write("\n");
            Iterator i$ = this.lruEntries.values().iterator();

            while(i$.hasNext()) {
                DiskLruCache.Entry entry = (DiskLruCache.Entry)i$.next();
                if (entry.currentEditor != null) {
                    writer.write("DIRTY " + entry.key + '\n');
                } else {
                    writer.write("CLEAN " + entry.key + entry.getLengths() + '\n');
                }
            }
        } finally {
            writer.close();
        }

        if (this.journalFile.exists()) {
            renameTo(this.journalFile, this.journalFileBackup, true);
        }

        renameTo(this.journalFileTmp, this.journalFile, false);
        this.journalFileBackup.delete();
        this.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.journalFile, true), Util.US_ASCII));
    }

    private static void deleteIfExists(File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException();
        }
    }

    private static void renameTo(File from, File to, boolean deleteDestination) throws IOException {
        if (deleteDestination) {
            deleteIfExists(to);
        }

        if (!from.renameTo(to)) {
            throw new IOException();
        }
    }

    public synchronized DiskLruCache.Value get(String key) throws IOException {
        this.checkNotClosed();
        DiskLruCache.Entry entry = (DiskLruCache.Entry)this.lruEntries.get(key);
        if (entry == null) {
            return null;
        } else if (!entry.readable) {
            return null;
        } else {
            File[] arr$ = entry.cleanFiles;
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                File file = arr$[i$];
                if (!file.exists()) {
                    return null;
                }
            }

            ++this.redundantOpCount;
            this.journalWriter.append("READ");
            this.journalWriter.append(' ');
            this.journalWriter.append(key);
            this.journalWriter.append('\n');
            if (this.journalRebuildRequired()) {
                this.executorService.submit(this.cleanupCallable);
            }

            return new DiskLruCache.Value(key, entry.sequenceNumber, entry.cleanFiles, entry.lengths);
        }
    }

    public DiskLruCache.Editor edit(String key) throws IOException {
        return this.edit(key, -1L);
    }

    private synchronized DiskLruCache.Editor edit(String key, long expectedSequenceNumber) throws IOException {
        this.checkNotClosed();
        DiskLruCache.Entry entry = (DiskLruCache.Entry)this.lruEntries.get(key);
        if (expectedSequenceNumber == -1L || entry != null && entry.sequenceNumber == expectedSequenceNumber) {
            if (entry == null) {
                entry = new DiskLruCache.Entry(key);
                this.lruEntries.put(key, entry);
            } else if (entry.currentEditor != null) {
                return null;
            }

            DiskLruCache.Editor editor = new DiskLruCache.Editor(entry);
            entry.currentEditor = editor;
            this.journalWriter.append("DIRTY");
            this.journalWriter.append(' ');
            this.journalWriter.append(key);
            this.journalWriter.append('\n');
            this.journalWriter.flush();
            return editor;
        } else {
            return null;
        }
    }

    public File getDirectory() {
        return this.directory;
    }

    public synchronized long getMaxSize() {
        return this.maxSize;
    }

    public synchronized void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        this.executorService.submit(this.cleanupCallable);
    }

    public synchronized long size() {
        return this.size;
    }

    private synchronized void completeEdit(DiskLruCache.Editor editor, boolean success) throws IOException {
        DiskLruCache.Entry entry = editor.entry;
        if (entry.currentEditor != editor) {
            throw new IllegalStateException();
        } else {
            int i;
            if (success && !entry.readable) {
                for(i = 0; i < this.valueCount; ++i) {
                    if (!editor.written[i]) {
                        editor.abort();
                        throw new IllegalStateException("Newly created entry didn't create value for index " + i);
                    }

                    if (!entry.getDirtyFile(i).exists()) {
                        editor.abort();
                        return;
                    }
                }
            }

            for(i = 0; i < this.valueCount; ++i) {
                File dirty = entry.getDirtyFile(i);
                if (success) {
                    if (dirty.exists()) {
                        File clean = entry.getCleanFile(i);
                        dirty.renameTo(clean);
                        long oldLength = entry.lengths[i];
                        long newLength = clean.length();
                        entry.lengths[i] = newLength;
                        this.size = this.size - oldLength + newLength;
                    }
                } else {
                    deleteIfExists(dirty);
                }
            }

            ++this.redundantOpCount;
            entry.currentEditor = null;
            if (entry.readable | success) {
                entry.readable = true;
                this.journalWriter.append("CLEAN");
                this.journalWriter.append(' ');
                this.journalWriter.append(entry.key);
                this.journalWriter.append(entry.getLengths());
                this.journalWriter.append('\n');
                if (success) {
                    entry.sequenceNumber = (long)(this.nextSequenceNumber++);
                }
            } else {
                this.lruEntries.remove(entry.key);
                this.journalWriter.append("REMOVE");
                this.journalWriter.append(' ');
                this.journalWriter.append(entry.key);
                this.journalWriter.append('\n');
            }

            this.journalWriter.flush();
            if (this.size > this.maxSize || this.journalRebuildRequired()) {
                this.executorService.submit(this.cleanupCallable);
            }

        }
    }

    private boolean journalRebuildRequired() {
        boolean redundantOpCompactThreshold = true;
        return this.redundantOpCount >= 2000 && this.redundantOpCount >= this.lruEntries.size();
    }

    public synchronized boolean remove(String key) throws IOException {
        this.checkNotClosed();
        DiskLruCache.Entry entry = (DiskLruCache.Entry)this.lruEntries.get(key);
        if (entry != null && entry.currentEditor == null) {
            for(int i = 0; i < this.valueCount; ++i) {
                File file = entry.getCleanFile(i);
                if (file.exists() && !file.delete()) {
                    throw new IOException("failed to delete " + file);
                }

                this.size -= entry.lengths[i];
                entry.lengths[i] = 0L;
            }

            ++this.redundantOpCount;
            this.journalWriter.append("REMOVE");
            this.journalWriter.append(' ');
            this.journalWriter.append(key);
            this.journalWriter.append('\n');
            this.lruEntries.remove(key);
            if (this.journalRebuildRequired()) {
                this.executorService.submit(this.cleanupCallable);
            }

            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean isClosed() {
        return this.journalWriter == null;
    }

    private void checkNotClosed() {
        if (this.journalWriter == null) {
            throw new IllegalStateException("cache is closed");
        }
    }

    public synchronized void flush() throws IOException {
        this.checkNotClosed();
        this.trimToSize();
        this.journalWriter.flush();
    }

    public synchronized void close() throws IOException {
        if (this.journalWriter != null) {
            Iterator i$ = (new ArrayList(this.lruEntries.values())).iterator();

            while(i$.hasNext()) {
                DiskLruCache.Entry entry = (DiskLruCache.Entry)i$.next();
                if (entry.currentEditor != null) {
                    entry.currentEditor.abort();
                }
            }

            this.trimToSize();
            this.journalWriter.close();
            this.journalWriter = null;
        }
    }

    private void trimToSize() throws IOException {
        while(this.size > this.maxSize) {
            java.util.Map.Entry<String, DiskLruCache.Entry> toEvict = (java.util.Map.Entry)this.lruEntries.entrySet().iterator().next();
            this.remove((String)toEvict.getKey());
        }

    }

    public void delete() throws IOException {
        this.close();
        Util.deleteContents(this.directory);
    }

    private static String inputStreamToString(InputStream in) throws IOException {
        return Util.readFully(new InputStreamReader(in, Util.UTF_8));
    }

    private final class Entry {
        private final String key;
        private final long[] lengths;
        File[] cleanFiles;
        File[] dirtyFiles;
        private boolean readable;
        private DiskLruCache.Editor currentEditor;
        private long sequenceNumber;

        private Entry(String key) {
            this.key = key;
            this.lengths = new long[DiskLruCache.this.valueCount];
            this.cleanFiles = new File[DiskLruCache.this.valueCount];
            this.dirtyFiles = new File[DiskLruCache.this.valueCount];
            StringBuilder fileBuilder = (new StringBuilder(key)).append('.');
            int truncateTo = fileBuilder.length();

            for(int i = 0; i < DiskLruCache.this.valueCount; ++i) {
                fileBuilder.append(i);
                this.cleanFiles[i] = new File(DiskLruCache.this.directory, fileBuilder.toString());
                fileBuilder.append(".tmp");
                this.dirtyFiles[i] = new File(DiskLruCache.this.directory, fileBuilder.toString());
                fileBuilder.setLength(truncateTo);
            }

        }

        public String getLengths() throws IOException {
            StringBuilder result = new StringBuilder();
            long[] arr$ = this.lengths;
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                long size = arr$[i$];
                result.append(' ').append(size);
            }

            return result.toString();
        }

        private void setLengths(String[] strings) throws IOException {
            if (strings.length != DiskLruCache.this.valueCount) {
                throw this.invalidLengths(strings);
            } else {
                try {
                    for(int i = 0; i < strings.length; ++i) {
                        this.lengths[i] = Long.parseLong(strings[i]);
                    }

                } catch (NumberFormatException var3) {
                    throw this.invalidLengths(strings);
                }
            }
        }

        private IOException invalidLengths(String[] strings) throws IOException {
            throw new IOException("unexpected journal line: " + Arrays.toString(strings));
        }

        public File getCleanFile(int i) {
            return this.cleanFiles[i];
        }

        public File getDirtyFile(int i) {
            return this.dirtyFiles[i];
        }
    }

    public final class Editor {
        private final DiskLruCache.Entry entry;
        private final boolean[] written;
        private boolean committed;

        private Editor(DiskLruCache.Entry entry) {
            this.entry = entry;
            this.written = entry.readable ? null : new boolean[DiskLruCache.this.valueCount];
        }

        private InputStream newInputStream(int index) throws IOException {
            DiskLruCache var2 = DiskLruCache.this;
            synchronized(DiskLruCache.this) {
                if (this.entry.currentEditor != this) {
                    throw new IllegalStateException();
                } else if (!this.entry.readable) {
                    return null;
                } else {
                    FileInputStream var10000;
                    try {
                        var10000 = new FileInputStream(this.entry.getCleanFile(index));
                    } catch (FileNotFoundException var5) {
                        return null;
                    }

                    return var10000;
                }
            }
        }

        public String getString(int index) throws IOException {
            InputStream in = this.newInputStream(index);
            return in != null ? DiskLruCache.inputStreamToString(in) : null;
        }

        public File getFile(int index) throws IOException {
            DiskLruCache var2 = DiskLruCache.this;
            synchronized(DiskLruCache.this) {
                if (this.entry.currentEditor != this) {
                    throw new IllegalStateException();
                } else {
                    if (!this.entry.readable) {
                        this.written[index] = true;
                    }

                    File dirtyFile = this.entry.getDirtyFile(index);
                    if (!DiskLruCache.this.directory.exists()) {
                        DiskLruCache.this.directory.mkdirs();
                    }

                    return dirtyFile;
                }
            }
        }

        public void set(int index, String value) throws IOException {
            OutputStreamWriter writer = null;

            try {
                OutputStream os = new FileOutputStream(this.getFile(index));
                writer = new OutputStreamWriter(os, Util.UTF_8);
                writer.write(value);
            } finally {
                Util.closeQuietly(writer);
            }

        }

        public void commit() throws IOException {
            DiskLruCache.this.completeEdit(this, true);
            this.committed = true;
        }

        public void abort() throws IOException {
            DiskLruCache.this.completeEdit(this, false);
        }

        public void abortUnlessCommitted() {
            if (!this.committed) {
                try {
                    this.abort();
                } catch (IOException var2) {
                    ;
                }
            }

        }
    }

    public final class Value {
        private final String key;
        private final long sequenceNumber;
        private final long[] lengths;
        private final File[] files;

        private Value(String key, long sequenceNumber, File[] files, long[] lengths) {
            this.key = key;
            this.sequenceNumber = sequenceNumber;
            this.files = files;
            this.lengths = lengths;
        }

        public DiskLruCache.Editor edit() throws IOException {
            return DiskLruCache.this.edit(this.key, this.sequenceNumber);
        }

        public File getFile(int index) {
            return this.files[index];
        }

        public String getString(int index) throws IOException {
            InputStream is = new FileInputStream(this.files[index]);
            return DiskLruCache.inputStreamToString(is);
        }

        public long getLength(int index) {
            return this.lengths[index];
        }
    }
}
