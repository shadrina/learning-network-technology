import React from 'react'
import axios from 'axios'

import { ChatFeed, Message } from 'react-chat-ui'

import AuthModal from '../AuthModal'

export default class Chat extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            isLoggedIn: false,
            myInfo: null,
            users: {},
            messages: []
        };

        this.loadUsers = this.loadUsers.bind(this);
        this.loadMessages = this.loadMessages.bind(this);
    }

    componentWillUnmount() {
        // Logout here
    }

    onMessageSubmit(e) {
        const input = this.message;
        e.preventDefault();
        if (!input.value) {
            return false;
        }
        this.pushMessage(this.state.myInfo['id'], input.value);
        input.value = '';
        return true;
    }

    pushMessage(recipient, message) {
        const prevState = this.state;
        const newMessage = new Message({
            id: recipient,
            message,
            senderName: this.state.users[recipient],
        });
        prevState.messages.push(newMessage);
        this.setState(this.state);
    }

    login(userData) {
        let users = this.state.users;
        users[0] = userData['name'];
        this.setState({
            isLoggedIn: true,
            myInfo: userData,
            users: users
        });

        this.loadUsers();
    }

    loadUsers() {
        let token = this.state.myInfo['token'];
        axios.get('/users?token=' + token)
            .then(response => {
                let users = this.state.users;
                response.data.forEach(user => {
                    users[user['id']] = user['username'];
                });
                this.setState({
                    users: users
                }, () => this.loadMessages());
            })
            .catch(error => console.error(error))
    }

    loadMessages() {
        let token = this.state.myInfo['token'];
        axios.get('/messages?token=' + token)
            .then(response => {
                let messages = this.state.messages;
                response.data.forEach(message => {
                    let id = message['author'];
                    if (id === this.state.myInfo['id']) {
                        id = 0;
                    }
                    messages.push(new Message({
                        id: id,
                        message: message['message'],
                        senderName: this.state.users[id]
                    }));
                });
                console.log(messages);
                this.setState({messages: messages});
            })
            .catch(error => console.error(error))
    }

    render() {
        return (
            <div>
                <AuthModal
                    show={!this.state.isLoggedIn}
                    onSuccess={this.login.bind(this)}
                />
                <div className="chatfeed-wrapper">
                    <ChatFeed
                        chatBubble={false}
                        maxHeight={700}
                        messages={this.state.messages}
                        showSenderName
                    />

                    <form onSubmit={e => this.onMessageSubmit(e)}>
                        <input
                            placeholder="Type a message..."
                        />
                    </form>
                </div>
            </div>
        );
    }
}