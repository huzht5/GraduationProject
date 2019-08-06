# -*- coding: utf-8 -*-
from django.http import HttpResponse
from django.shortcuts import render
from django.views.decorators.csrf import csrf_exempt
from myapp import models
import json

# 解析操作码、取出POST请求体中的数据并调用相应的函数
@csrf_exempt
def select(request, opcode):
    response = ""
    if opcode=="register":
        accountid=request.POST.get('accountid','')
        password=request.POST.get('password','')
        name=request.POST.get('name','')
        information=request.POST.get('information','')
        count=request.POST.get('count','')
        response = register(accountid, password, name, information, count)
    elif opcode=="follow_club":
        followerid=request.POST.get('followerid','')
        name=request.POST.get('name','')
        response = follow_club(followerid, name)
    elif opcode=="follow_account":
        followerid=request.POST.get('followerid','')
        accountid=request.POST.get('accountid','')
        response = follow_account(followerid,accountid)
    elif opcode=="create_club":
        ownerid=request.POST.get('ownerid','')
        ownername=request.POST.get('ownername','')
        name=request.POST.get('name','')
        information=request.POST.get('information','')
        count=request.POST.get('count','')
        response = create_club(ownerid, ownername, name, information, count)
    elif opcode=="create_account_blog":
        accountid=request.POST.get('accountid','')
        time=request.POST.get('time','')
        message=request.POST.get('message','')
        response = create_account_blog(accountid, time, message)
    elif opcode=="create_club_blog":
        name=request.POST.get('name','')
        time=request.POST.get('time','')
        message=request.POST.get('message','')
        response = create_club_blog(name, time, message)
    elif opcode=="create_club_sign":
        name=request.POST.get('name','')
        time=request.POST.get('time','')
        message=request.POST.get('message','')
        state=request.POST.get('state','')
        response = create_club_sign(name, time, message, state)
    elif opcode=="sign":
        accountid=request.POST.get('accountid','')
        name=request.POST.get('name','')
        time=request.POST.get('time','')
        signtime=request.POST.get('signtime','')
        response = sign(accountid, name, time, signtime)
    elif opcode=="send_message":
        senderid=request.POST.get('senderid','')
        receiverid=request.POST.get('receiverid','')
        time=request.POST.get('time','')
        message=request.POST.get('message','')
        response = send_message(senderid, receiverid, time, message)
    else:
        response = "wrongopcode"
    return HttpResponse(response)


# 注册。如果账号已被注册，返回字符串fail；如果注册成功，则返回字符串registered
def register(accountid1, password1, name1, information1, count1):
    # 查找Account表
    v1 = models.Account.objects.filter(accountid=accountid1)
    if len(v1):
        return "fail"
    else:
        # 在Account表增加一项
        models.Account.objects.create(accountid=accountid1, password=password1, name=name1, information=information1, count=count1)
        return "registered"

# 关注社团。如果成功关注，则返回字符串followclub
def follow_club(followerid1, name1):
    # 如果要关注的社团已存在且未关注，则在ClubFollow表中增加一项数据，然后把社团的粉丝数加一。否则，关注社团失败
    v1 = models.Club.objects.filter(name=name1)
    v2 = models.ClubFollow.objects.filter(name=name1, followerid=followerid1)
    if len(v1) and not(len(v2)):
        models.ClubFollow.objects.create(name=name1, followerid=followerid1)
        c = v1[0].count+1
        models.Club.objects.filter(name=name1).update(count=c)
        return "followclub"
    else:
        return "fail"

# 关注用户。参数followerid1是请求数据的用户的账号，参数accountid1是对方用户的账号。如果成功关注，则返回字符串followaccount
def follow_account(followerid1, accountid1):
    # 如果要关注的用户已存在且未关注，则在AccountFollow表中增加一项数据，然后把对方用户的粉丝数加一。否则，关注用户失败
    v1 = models.Account.objects.filter(accountid=accountid1)
    v2 = models.AccountFollow.objects.filter(accountid=accountid1, followerid=followerid1)
    if len(v1) and not(len(v2)):
        models.AccountFollow.objects.create(accountid=accountid1, followerid=followerid1)
        c = v1[0].count+1
        models.Account.objects.filter(accountid=accountid1).update(count=c)
        return "followaccount"
    else:
        return "fail"

# 创建社团。如果社团名已被使用，返回字符串fail；如果创建成功，则返回字符串createclub
def create_club(ownerid1, ownername1, name1, information1, count1):
    v1 = models.Club.objects.filter(name=name1)
    if len(v1):
        return "fail"
    else:
        models.Club.objects.create(name=name1, ownerid=ownerid1, ownername=ownername1, information=information1, count=count1)
        models.ClubFollow.objects.create(name=name1, followerid=ownerid1)
        return "createclub"

# 发布个人动态。如果发布成功，则返回字符串createaccountblog
def create_account_blog(accountid1, time1, message1):
    # 如果要发布个人动态的用户已存在且不存在相同时间的个人动态，则在AccountBlog表中增加一项数据。否则，发布个人动态失败
    v1 = models.Account.objects.filter(accountid=accountid1)
    v2 = models.AccountBlog.objects.filter(accountid=accountid1, time=time1)
    if len(v1) and not(len(v2)):
        models.AccountBlog.objects.create(accountid=accountid1, time=time1, message=message1)
        return "createaccountblog"
    else:
        return "fail"

# 发布社团公告。如果发布成功，则返回字符串createclubblog
def create_club_blog(name1, time1, message1):
    # 如果要发布社团公告的社团已存在且不存在相同时间的社团公告，则在ClubBlog表中增加一项数据。否则，发布社团公告失败
    v1 = models.Club.objects.filter(name=name1)
    v2 = models.ClubBlog.objects.filter(name=name1, time=time1)
    if len(v1) and not(len(v2)):
        models.ClubBlog.objects.create(name=name1, time=time1, message=message1)
        return "createclubblog"
    else:
        return "fail"

# 发布签到活动。如果发布成功，则返回字符串createclubsign
def create_club_sign(name1, time1, message1, state1):
    # 如果要发布签到活动的社团已存在且不存在相同时间的签到活动，则在ClubSign表中增加一项数据。否则，发布签到活动失败
    v1 = models.Club.objects.filter(name=name1)
    v2 = models.ClubSign.objects.filter(name=name1, time=time1)
    if len(v1) and not(len(v2)):
        models.ClubSign.objects.create(name=name1, time=time1, message=message1, state=state1)
        return "createclubsign"
    else:
        return "fail"

# 签到。如果已签到，则返回字符串signed；如果签到成功，则返回字符串sign
def sign(accountid1, name1, time1, signtime1):
    v1 = models.AccountSign.objects.filter(accountid=accountid1, name=name1, time=time1)
    v2 = models.ClubSign.objects.filter(name=name1, time=time1)
    if not(len(v2)) or v2[0].state=="签到结束":
        return "fail"
    elif len(v1):
        return "signed"
    else:
        models.AccountSign.objects.create(accountid=accountid1, name=name1, time=time1, signtime=signtime1)
        return "sign"

# 发送信息。如果发送成功，则返回字符串sendmessage
def send_message(senderid1, receiverid1, time1, message1):
    v1 = models.Account.objects.filter(accountid=senderid1)
    v2 = models.Account.objects.filter(accountid=receiverid1)
    if len(v1) and len(v2):
        models.AccountMessage.objects.create(senderid=senderid1, receiverid=receiverid1, time=time1, message=message1)
        return "sendmessage"
    else:
        return "fail"