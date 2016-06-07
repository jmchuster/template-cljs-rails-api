class ApplicationController < ActionController::API
  include ActionController::Cookies

  attr_reader :current_user

  private

  def authenticate_user_token!
    @current_user = UsersService.user_from_token(cookies[:user_token])
    render status: 401, json: {
      errors: [{
        status: "401",
        title: "Unauthorized",
        detail: "User must first login."
      }]
    } if not @current_user
  end
end
