class User < ApplicationRecord
  has_secure_password

  validates_presence_of :password_confirmation, on: :create
  validates_presence_of :email
end
