from django.urls import path
from django.conf.urls import url
from django.views.static import serve
from django.conf import settings
from myapp import insert,update,delete,query

urlpatterns = [
    url(r'^insert/(.+)/$', insert.select),
	url(r'^update/(.+)/$', update.select),
	url(r'^delete/(.+)/$', delete.select),
	url(r'^query/(.+)/$', query.select),
	url(r'^download/(?P<path>.*)$', serve, {'document_root': 'download/'}),
]