export let websocket = null;

export function createWebSocket(url, callBackFunction){
    websocket = new WebSocket(url);
    websocket.onmessage = function(event){
        callBackFunction(event);
    }
    websocket.onclose = function(error){
        console.log("websocket链接已断开: "+error.reason);
    }
    websocket.onerror = () => {
        //如果链接失败，尝试重新链接
        setTimeout(function() {
            createWebSocket(url, callBackFunction);
        }, 5000);
    }
}

export function closeWebSocket(){
    if(websocket) websocket.close();
}

