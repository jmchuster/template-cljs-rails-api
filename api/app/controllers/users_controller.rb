class UsersController < ApplicationController
  before_action :authenticate_user_token!

  def index
    render json: {
      data: [
        User.all.map do |user|
          {
            type: 'user',
            id: user.id,
            email: user.email
          }
        end
      ]
    }
  end

end
