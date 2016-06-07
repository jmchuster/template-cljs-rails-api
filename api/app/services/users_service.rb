module UsersService
  module_function

  def user_from_login(email, password)
    User.find_by(email: email).try(:authenticate, password)
  end

  def create_user_token(user, exp = 1.week.from_now)
    payload = {
      user_id: user.id,
      exp: exp.to_i
    }
    JWT.encode(payload, Rails.application.secrets.secret_key_base)
  end

  def user_from_token(token)
    payload = JWT.decode(token, Rails.application.secrets.secret_key_base)[0]
    user = User.find(payload["user_id"])
  rescue
    # JWT is invalid or JWT is expired
    nil
  end
end
