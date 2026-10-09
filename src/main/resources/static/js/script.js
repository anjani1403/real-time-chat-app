let stompClient = null;

function setConnected(connected){
    document.getElementById('sendMessage').disabled = !connected;
}

function connect(){
    var socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);

        // Subscribe to chat messages
        stompClient.subscribe('/topic/messages', function (message){
            showMessage(JSON.parse(message.body));
        });

        // Subscribe to typing events
        stompClient.subscribe('/topic/typing', function (message){
            showTyping(JSON.parse(message.body));
        });

        loadHistory();
    });
}

function showMessage(message) {
    var chat = document.getElementById('chat');
    var messageElement = document.createElement('div');
    messageElement.textContent = message.sender + ' : ' + message.content;
    messageElement.className = "border-bottom mb-1";
    chat.appendChild(messageElement);
    chat.scrollTop = chat.scrollHeight;
}

function sendMessage() {
    var sender = document.getElementById('senderInput').value;
    var content = document.getElementById('messageInput').value;
    var chatMessage = {
        sender : sender,
        content : content
    }
    stompClient.send("/app/sendMessage", {}, JSON.stringify(chatMessage));
    document.getElementById('messageInput').value = '';
}

document.getElementById('sendMessage').onclick = sendMessage;
window.onload = connect;

function showTyping(message) {
    var indicator = document.getElementById('typingIndicator');

    if (message.type === "TYPING") {
        indicator.textContent = message.sender + " is typing...";
    } else if (message.type === "STOP_TYPING") {
        indicator.textContent = "";
    }
}

var typingTimeout;

document.getElementById('messageInput').addEventListener('input', function() {
    var sender = document.getElementById('senderInput').value;

    // Send typing event
    stompClient.send("/app/typing", {}, JSON.stringify({ sender: sender }));

    clearTimeout(typingTimeout);
    typingTimeout = setTimeout(function() {
        stompClient.send("/app/stopTyping", {}, JSON.stringify({ sender: sender }));
    }, 4000); // stop typing after 4 second of inactivity
});

function loadHistory() {
    fetch('/api/messages')
        .then(response => response.json())
        .then(messages => messages.forEach(showMessage))
        .catch(error => console.error('Could not load history', error));
}