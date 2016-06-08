Rails.application.routes.draw do
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html

  get '/api', to: 'api#index'

  scope '/api' do
    resource :session, only: [:show, :create]
    resources :users
  end
end
