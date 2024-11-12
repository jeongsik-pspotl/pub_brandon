const g_config = {
    WEBSOCKETSERVER_URL: (function() {
        const hostIP = location.hostname;
        const hostProtocol = location.protocol;
        const websocketPort = ':'+location.port;
        let websocketURL = '';
        if(hostProtocol === 'http:') {
            websocketURL = 'ws://' + hostIP + websocketPort;
        } else if (hostProtocol === 'https:') {
            websocketURL = 'wss://' + hostIP + websocketPort;
        }
        return websocketURL;
    }()),
    HTTPSERVER_URL: (function(){
        const hostIP = location.hostname;
        const hostProtocol = location.protocol;
        const managerPort = ':'+location.port;
        const headquaterPort = ':8088'; // 변경 필요??? 우선 변경 하지 말고 기능구현 -> 테스트 진행
        const httpURL = hostProtocol + "//" + hostIP + managerPort;
        return httpURL;
    }()),
    PROFILES: (function(){
        const profile = "service" // service, onpremise
        return profile;
    }())
};