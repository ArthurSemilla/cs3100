from django.conf.urls import patterns, include, url
from django.contrib import admin

admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'chalkboard.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    url(r'^admin/', include(admin.site.urls)),
    url(r'^add/course/', 'tracker.views.addCourse'),
    url(r'^add/category/', 'tracker.views.addCategory'),
    url(r'^add/homework/', 'tracker.views.addHomework'),
    url(r'^add/grade/', 'tracker.views.addGrade'),
    url(r'^add/user/', 'tracker.views.register' ),

    url(r'^get/course/', 'tracker.views.getCourse'),
    url(r'^get/grade/', 'tracker.views.getGrade'),
    url(r'^get/category/', 'tracker.views.getCategory'),
    url(r'^get/homework/', 'tracker.views.getHomework'),

    url(r'^rm/grade/', 'tracker.views.rmGrade'),

    url(r'^edit/course/', 'tracker.views.editCourse'),
    url(r'^edit/grade/', 'tracker.views.editGrade'),
    url(r'^edit/category/', 'tracker.views.editCategory'),
    url(r'^edit/homework/', 'tracker.views.editHomework'),

    url(r'^get/course2cat/', 'tracker.views.getCategoriesForCourse'),

    url('^$', 'tracker.views.index'),

    url('^', include('django.contrib.auth.urls', namespace="auth")), 
    url('^', include('registration.backends.default.urls')),
    url(r'', include('tokenapi.urls')),
)
