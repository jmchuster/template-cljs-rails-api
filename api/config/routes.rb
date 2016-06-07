Rails.application.routes.draw do
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html

  get '/api', to: 'api#index'

  scope '/api' do
    resources :sessions
    resources :users
  end
end
