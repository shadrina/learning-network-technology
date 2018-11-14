from flask import Flask
from views import blueprint

def create_app():
    app = Flask(__name__)
    app.register_blueprint(blueprint)
    return app


app = create_app()


if __name__ == '__main__':
    app.run(threaded=True)
