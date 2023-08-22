from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
from itsdangerous import BadSignature, SignatureExpired
from flask import current_app, request, make_response
from functools import wraps


def generate_auth_token(identity, expiration=3600):
    s = Serializer(current_app.config["SECRET_KEY"], expires_in=expiration)
    return s.dumps({'identity': identity})


def verify_auth_token(token):
    s = Serializer(current_app.config["SECRET_KEY"])
    try:
        data = s.loads(token)
    except SignatureExpired:
        return None  # Token válido, mas expirado
    except BadSignature:
        return None  # Token inválido
    return data['identity']


def token_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        user = None
        if request.authorization:
            user = verify_auth_token(request.authorization.username)
        if user is None:
            current_app.logger.error('Token expirado...')
            return make_response({"msg_erro": "Token expirado ou inválido!"}, 401)
        return f(*args, **kwargs)
    return decorated_function
