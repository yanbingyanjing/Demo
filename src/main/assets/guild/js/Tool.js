/// 获取用户信息
var getUserInfoCallback;
function getUserInfoNative(callback) {
  getUserInfoCallback = callback;
  var json = {
    param: {},
    callback: "getUserInfo",
  };
  noticeNative(json);

  // setTimeout(function () {
  //   var json = {
  //     callback: "getUserInfo",
  //     param: {},
  //     data: {
  //       "XX-Api-Version": "1.2.7",
  //       "XX-Device-Type": "iphone",
  //       userid: "5234957",
  //       "XX-Token":
  //         "6a3362a9dc7aa5c85f2f8c02489bd1a6e56ef1d50ec0b5d927533c777ac91739",
  //     },
  //   };
  //   json["data"] = getUserInfoCallback(json);
  // }, 100);
}
function getUserInfo(json) {
  getUserInfoCallback(json);
}

/// load
function showLoading() {
  var json = {
    callback: "showLoading",
    param: {},
  };
  noticeNative(json);
}
function hideLoading() {
  var json = {
    callback: "hideLoading",
    param: {},
  };
  noticeNative(json);
}

/// alert
function alertPrompt(title) {
  var json = {
    callback: "hideLoading",
    param: {
      title: title,
    },
  };
  noticeNative(json);
}

/// 获取图片
var receiveImgUrlCallback;
function receiveImgUrlNative(param, callback) {
  receiveImgUrlCallback = callback;
  var json = {
    param: param,
    callback: "receiveImgUrl",
  };
  noticeNative(json);
}
function receiveImgUrl(json) {
  receiveImgUrlCallback(json);
}

/// 通知原生
function noticeNative(json) {
  if (isIphone()) {
    window.webkit.messageHandlers.sendMessage.postMessage(JSON.stringify(json));
  } else if (isAndroid()) {
    window.android.sendMessage(JSON.stringify(json));
  }
}

/// 判断机型

function isAndroid() {
  var ua = navigator.userAgent.toLowerCase();
  var isA = ua.indexOf("android") > -1;
  if (isA) {
    return true;
  }
  return false;
}

function isIphone() {
  var ua = navigator.userAgent.toLowerCase();
  var isIph = ua.indexOf("iphone") > -1;
  if (isIph) {
    return true;
  }
  return false;
}

function tokeninit(callback) {
  localStorage.clear();
  var token = localStorage.getItem("XX-Token");
  if (token == null) {
    getUserInfoNative(function (json) {
      localStorage.setItem("XX-Token", json.data["XX-Token"]);
      localStorage.setItem("XX-Device-Type", json.data["XX-Device-Type"]);
      localStorage.setItem("domain", json.data["domain"]);
      localStorage.setItem("cosdomain", json.data["cosdomain"]);
      localStorage.setItem("coin_zh", json.data["coin_zh"]);
      callback();
    });
  } else {
    callback();
  }
}

function getQueryVariable(variable) {
  var query = window.location.search.substring(1);
  var vars = query.split("&");
  for (var i = 0; i < vars.length; i++) {
    var pair = vars[i].split("=");
    if (pair[0] == variable) {
      return pair[1];
    }
  }
  return false;
}
