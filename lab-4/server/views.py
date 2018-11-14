from flask import Blueprint, render_template, request
from database import db
from util import create_token
import json

blueprint = Blueprint('blueprint', __name__,
                      static_folder="../client/dist",
                      template_folder="../client/static")

@blueprint.route('/', defaults={'path': ''})
@blueprint.route('/<path:path>')
def index(path):
    return render_template('index.html')

@blueprint.route('/login', methods=['POST'])
def login_handler():
    login = request.json.get('login')
    groupId = request.json.get('groupId')

    # Check if there is user with the same name
    select = db.prepare("SELECT * FROM users WHERE name=$1")
    user = select(login)
    if user:
        return "Username is already in use", 401

    token = create_token(login)
    insert = db.prepare("INSERT INTO users VALUES (DEFAULT, $1, $2, $3, $4)")
    insert(login, True, groupId, token)
    user = select(login)[0]
    answer = {
        'id': user[0],
        'username': user[1],
        'online': user[2],
        'token': user[4]
    }
    return json.dumps(answer), 200

@blueprint.route('/logout', methods=['POST'])
def logout_handler():
    token = request.json.get('token')
    delete = db.prepare("DELETE FROM users WHERE token=$1")
    delete(token)
    return "", 200

@blueprint.route('/users', methods=['GET'])
def users_handler():
    token = request.args.get('token')
    # Check if there is user with the the token
    select = db.prepare("SELECT * FROM users WHERE token=$1")
    user_with_token = select(token)
    if not user_with_token:
        return "Токен неизвестен серверу", 403

    users = db.query("SELECT * FROM users WHERE online=true")
    answer = map(lambda user: {
        'id': user[0],
        'username': user[1],
        'online': user[2]
    }, users)
    return json.dumps(list(answer)), 200

@blueprint.route('/users/<id>', methods=['GET'])
def user_handler():
    return "", 200

@blueprint.route('/messages', methods=['GET'])
def messages_handler():
    token = request.args.get('token')
    # Check if there is user with the the token
    select = db.prepare("SELECT * FROM users WHERE token=$1")
    user = select(token)
    if not user:
        return "Токен неизвестен серверу", 403

    messages = db.query("SELECT * FROM messages")
    answer = map(lambda message: {
        'id': message[0],
        'message': message[1],
        'author': message[2]
    }, messages)
    return json.dumps(list(answer)), 200
