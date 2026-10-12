// ---------- State ----------
let stompClient = null;
let currentRoomId = null;          // the room the user is currently in
let messageSubscription = null;    // subscription to the room's message topic
let typingSubscription = null;     // subscription to the room's typing topic
let typingTimeout = null;
const shownMessageIds = new Set(); // ids already on screen, to avoid duplicates

// ---------- Connection ----------
function setConnected(connected) {
    document.getElementById('sendMessage').disabled = !connected;
}

function connect() {
    const socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        loadRooms();   // once connected, fetch rooms and join the first one
    });
}

// ---------- Rooms ----------
function loadRooms() {
    fetch('/api/rooms')
        .then(response => response.json())
        .then(rooms => {
            const select = document.getElementById('roomSelect');
            select.replaceChildren();
            rooms.forEach(room => {
                const option = document.createElement('option');
                option.value = room.id;
                option.textContent = room.name;   // textContent keeps it XSS-safe
                select.appendChild(option);
            });
            if (rooms.length > 0) {
                joinRoom(rooms[0].id);
            }
        })
        .catch(error => console.error('Could not load rooms', error));
}

function joinRoom(roomId) {
    // Leave the previous room
    if (messageSubscription) messageSubscription.unsubscribe();
    if (typingSubscription) typingSubscription.unsubscribe();
    clearTimeout(typingTimeout);

    // Reset the screen for the new room
    currentRoomId = roomId;
    shownMessageIds.clear();
    document.getElementById('chat').replaceChildren();
    document.getElementById('typingIndicator').textContent = '';

    // Subscribe to this room's topics (subscribe first, then load history)
    messageSubscription = stompClient.subscribe('/topic/rooms/' + roomId + '/messages', function (message) {
        showMessage(JSON.parse(message.body));
    });
    typingSubscription = stompClient.subscribe('/topic/rooms/' + roomId + '/typing', function (message) {
        showTyping(JSON.parse(message.body));
    });

    loadHistory(roomId);
}

function loadHistory(roomId) {
    fetch('/api/rooms/' + roomId + '/messages')
        .then(response => response.json())
        .then(messages => {
            // The user may have switched rooms while this was loading
            if (roomId !== currentRoomId) return;
            messages.forEach(showMessage);
        })
        .catch(error => console.error('Could not load history', error));
}

// ---------- Messages ----------
function showMessage(message) {
    // Skip a message we already display (history vs live overlap)
    if (message.id != null) {
        if (shownMessageIds.has(message.id)) return;
        shownMessageIds.add(message.id);
    }
    const chat = document.getElementById('chat');
    const messageElement = document.createElement('div');
    messageElement.textContent = message.sender + ' : ' + message.content;
    messageElement.className = 'border-bottom mb-1';
    chat.appendChild(messageElement);
    chat.scrollTop = chat.scrollHeight;
}

function sendMessage() {
    if (currentRoomId === null) return;
    const sender = document.getElementById('senderInput').value.trim();
    const input = document.getElementById('messageInput');
    const content = input.value.trim();
    if (!sender || !content) return;   // basic check; real validation comes in a later module

    stompClient.send('/app/rooms/' + currentRoomId + '/sendMessage', {}, JSON.stringify({ sender: sender, content: content }));
    input.value = '';
    clearTimeout(typingTimeout);
    sendStopTyping();
}

// ---------- Typing indicator ----------
function showTyping(message) {
    const indicator = document.getElementById('typingIndicator');
    if (message.type === 'TYPING') {
        indicator.textContent = message.sender + ' is typing...';
    } else if (message.type === 'STOP_TYPING') {
        indicator.textContent = '';
    }
}

function sendStopTyping() {
    if (currentRoomId === null || !stompClient || !stompClient.connected) return;
    const sender = document.getElementById('senderInput').value;
    stompClient.send('/app/rooms/' + currentRoomId + '/stopTyping', {}, JSON.stringify({ sender: sender }));
}

// ---------- Event listeners ----------
document.getElementById('sendMessage').onclick = sendMessage;

document.getElementById('roomSelect').addEventListener('change', function () {
    joinRoom(Number(this.value));
});

document.getElementById('messageInput').addEventListener('input', function () {
    if (currentRoomId === null || !stompClient || !stompClient.connected) return;
    const sender = document.getElementById('senderInput').value;
    stompClient.send('/app/rooms/' + currentRoomId + '/typing', {}, JSON.stringify({ sender: sender }));

    clearTimeout(typingTimeout);
    typingTimeout = setTimeout(sendStopTyping, 4000);   // stop after 4 seconds of inactivity
});

window.onload = connect;