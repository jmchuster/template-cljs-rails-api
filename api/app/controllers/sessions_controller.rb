class SessionsController < ApplicationController
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
      render status: 401, json: {
        errors: [{
          status: "401",
          title: "Unauthorized",
          detail: "User could not be authorized."
        }]
      }
    end
  end

  alias index create
end
