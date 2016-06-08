class SessionsController < ApplicationController
  def show
    authenticate_user_token!
    if current_user
      render status: 200, json: {
        data: {
          type: 'user',
          id: current_user.id,
          email: current_user.email
        }
      }
    end
  end

  def create
    user = UsersService.user_from_login(params[:email], params[:password])
    if user
      cookies[:user_token] = UsersService.create_user_token(user)
      render status: 200, json: {
        data: {
          type: 'user',
          id: user.id,
          email: user.email
        }
      }
    else
      cookies[:user_token] = nil
      render status: 401, json: {
        errors: [{
          status: "401",
          title: "Unauthorized",
          detail: "User could not be authorized."
        }]
      }
    end
  end
end
