class UsersController < ApplicationController
  before_action :authenticate_user_token!

  def index
    render plain: "Current user id is #{current_user.id}"
  end

end
