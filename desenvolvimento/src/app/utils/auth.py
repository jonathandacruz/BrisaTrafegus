import jwt
from datetime import datetime, timedelta
from flask import current_app, request
from functools import wraps


def generate_jwt_token(identity):
    try:
        header = {
            'alg': 'HS256',
            'typ': 'JWT'
        }
        payload = {
            'exp': datetime.utcnow() + timedelta(days=3),
            'iat': datetime.utcnow(),
            'sub': identity
        }

        token = jwt.encode(
            payload,
            current_app.config['SECRET_KEY'],
            algorithm='HS256',
            headers=header
        )

        return token
    except Exception as e:
        return str(e)


def verify_jwt_token(token):
    try:
        payload = jwt.decode(
            token,
            current_app.config['SECRET_KEY'],
            algorithms=["HS256"]
        )
        return payload['sub']
    except jwt.ExpiredSignatureError:
        current_app.logger.error("Token expired: %s", token)
        return None
    except jwt.InvalidTokenError as e:
        current_app.logger.error("Invalid token: %s, error: %s", token, str(e))
        return None



def token_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        token = request.headers.get('Authorization')

        if not token:
            return {"message": "Token is missing"}, 401

        identity = verify_jwt_token(token.split()[1])

        if identity is None:
            return {"message": "Invalid or expired token"}, 401

        return f(identity, *args, **kwargs)

    return decorated_function
