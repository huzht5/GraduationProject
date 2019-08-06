# -*- coding: utf-8 -*-
from __future__ import unicode_literals
 
from django.db import models
from django.utils.encoding import python_2_unicode_compatible


'''
用户表
accountid   表示账号
password    表示密码
name        表示用户昵称
information 表示个人简介
count       表示粉丝数
'''
@python_2_unicode_compatible
class Account(models.Model):
    accountid = models.CharField(max_length=40)
    password = models.CharField(max_length=40)
    name = models.CharField(max_length=40)
    information = models.CharField(max_length=200)
    count = models.IntegerField()

    def __str__(self):
        return self.name


'''
社团表
name        表示社团名
ownerid     表示社长账号
ownername   表示社长用户昵称
information 表示社团简介
count       表示粉丝数
'''
@python_2_unicode_compatible
class Club(models.Model):
    name = models.CharField(max_length=40)
    ownerid = models.CharField(max_length=40)
    ownername = models.CharField(max_length=40)
    information = models.CharField(max_length=200)
    count = models.IntegerField()

    def __str__(self):
        return self.name


'''
用户关注表
accountid  表示被关注人的账号
followerid 表示关注人的账号
'''
@python_2_unicode_compatible
class AccountFollow(models.Model):
    accountid = models.CharField(max_length=40)
    followerid = models.CharField(max_length=40)

    def __str__(self):
        return self.name


'''
社团关注表
name       表示被关注社团的名字
followerid 表示关注人的账号
'''
@python_2_unicode_compatible
class ClubFollow(models.Model):
    name = models.CharField(max_length=40)
    followerid = models.CharField(max_length=40)

    def __str__(self):
        return self.name


'''
用户动态表
accountid 表示账号
time      表示发布时间
message   表示动态内容
默认查找排序：按time从晚到早
'''
@python_2_unicode_compatible
class AccountBlog(models.Model):
    accountid = models.CharField(max_length=40)
    time = models.CharField(max_length=40)
    message = models.CharField(max_length=200)

    def __str__(self):
        return self.name
		
    class Meta:
        ordering = ['-time']


'''
社团公告表
name    表示社团名
time    表示发布时间
message 表示公告内容
默认查找排序：按time从晚到早
'''
@python_2_unicode_compatible
class ClubBlog(models.Model):
    name = models.CharField(max_length=40)
    time = models.CharField(max_length=40)
    message = models.CharField(max_length=200)

    def __str__(self):
        return self.name
		
    class Meta:
        ordering = ['-time']


'''
社团签到表
name    表示社团名
time    表示发布时间
message 表示签到内容
state   表示是否可签到
默认查找排序：按time从晚到早
'''
@python_2_unicode_compatible
class ClubSign(models.Model):
    name = models.CharField(max_length=40)
    time = models.CharField(max_length=40)
    message = models.CharField(max_length=200)
    state = models.CharField(max_length=40)

    def __str__(self):
        return self.name
		
    class Meta:
        ordering = ['-time']
		
		
'''
用户签到表
accountid 表示账号
name      表示社团名
time      表示发布时间
signtime  表示签到时间
默认查找排序：按time从晚到早
'''
@python_2_unicode_compatible
class AccountSign(models.Model):
    accountid = models.CharField(max_length=40)
    name = models.CharField(max_length=40)
    time = models.CharField(max_length=40)
    signtime = models.CharField(max_length=40)

    def __str__(self):
        return self.name
		
    class Meta:
        ordering = ['-time']
		
		
'''
消息表
senderid   表示发送者账号
receiverid 表示接收者账号
time       表示发送时间
message    表示消息内容
默认查找排序：按time从早到晚
'''
@python_2_unicode_compatible
class AccountMessage(models.Model):
    senderid = models.CharField(max_length=40)
    receiverid = models.CharField(max_length=40)
    time = models.CharField(max_length=40)
    message = models.CharField(max_length=40)

    def __str__(self):
        return self.name
		
    class Meta:
        ordering = ['time']