from google.appengine.ext.webapp import template

import webapp2
import json
from models.user import User


class logInManager(webapp2.RequestHandler):
	def get(self):
		template_params={}
		mail=self.request.get('mail')
		password=self.request.get('password')
		
		user= User.query(User.mail == mail).get()
		if not user:
			self.error(403)
			self.response.write('Wrong username')
			return
		if not user.checkPassword(password):
			self.error(403)
			self.response.write('Wrong password')
			return
		template_params['status']="OK"
		template_params['user']=user.mail
		template_params['type']="manager"
		self.response.write(json.dumps(template_params))

class logInWorker(webapp2.RequestHandler):
	def get(self):
		template_params={}
		code=self.request.get('code')
		
		user= User.query(User.code == code).get()
		if not user:
			self.error(403)
			self.response.write('Wrong code')
			return
		template_params['status']="OK"
		template_params['user']=user.mail
		template_params['type']="worker"
		self.response.write(json.dumps(template_params))

app = webapp2.WSGIApplication([
	('/api/loginManager',logInManager),
	('/api/loginCode', logInWorker)
], debug=True)