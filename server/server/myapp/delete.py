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
    if opcode=="cancel_follow_club":
        followerid=request.POST.get('followerid','')
        name=request.POST.get('name','')
        response = cancel_follow_club(followerid, name)
    elif opcode=="cancel_follow_account":
        followerid=request.POST.get('followerid','')
        accountid=request.POST.get('accountid','')
        response = cancel_follow_account(followerid, accountid)
    elif opcode=="delete_club":
        name=request.POST.get('name','')
        response = delete_club(name)
    elif opcode=="delete_account_blog":
        accountid=request.POST.get('accountid','')
        time=request.POST.get('time','')
        response = delete_account_blog(accountid, time)
    elif opcode=="delete_club_blog":
        name=request.POST.get('name','')
        time=request.POST.get('time','')
        response = delete_club_blog(name, time)
    elif opcode=="delete_club_sign":
        name=request.POST.get('name','')
        time=request.POST.get('time','')
        response = delete_club_sign(name, time)
    else:
        response = "wrongopcode"
    return HttpResponse(response)


# 取消关注社团。如果成功取消关注，则返回字符串cancelfollowclub
def cancel_follow_club(followerid1, name1):
    v = models.Account.objects.filter(accountid=followerid1)
    v1 = models.Club.objects.filter(name=name1)
    v2 = models.ClubFollow.objects.filter(name=name1, followerid=followerid1)
    if len(v) and len(v1) and len(v2):
        models.ClubFollow.objects.filter(name=name1, followerid=followerid1).delete()
        c = v1[0].count-1
        models.Club.objects.filter(name=name1).update(count=c)
        return "cancelfollowclub"
    else:
        return "fail"

# 取消关注用户。参数followerid1是请求数据的用户的账号，参数accountid1是对方用户的账号。如果成功取消关注，则返回字符串cancelfollowaccount
def cancel_follow_account(followerid1,accountid1):
    v = models.Account.objects.filter(accountid=followerid1)
    v1 = models.Account.objects.filter(accountid=accountid1)
    v2 = models.AccountFollow.objects.filter(accountid=accountid1, followerid=followerid1)
    if len(v) and len(v1) and len(v2):
        # 在AccountFollow表中删除一项数据，然后把对方用户的粉丝数减一
        models.AccountFollow.objects.filter(accountid=accountid1, followerid=followerid1).delete()
        c = v1[0].count-1
        models.Account.objects.filter(accountid=accountid1).update(count=c)
        return "cancelfollowaccount"
    else:
        return "fail"

# 删除社团。如果删除成功，则返回字符串deleteclub
def delete_club(name1):
    v = models.Club.objects.filter(name=name1)
    if len(v):
        models.Club.objects.filter(name=name1).delete()
        models.ClubFollow.objects.filter(name=name1).delete()
        models.ClubBlog.objects.filter(name=name1).delete()
        models.ClubSign.objects.filter(name=name1).delete()
        models.AccountSign.objects.filter(name=name1).delete()
        return "deleteclub"
    else:
        return "fail"

# 删除个人动态。如果删除成功，则返回字符串deleteaccountblog
def delete_account_blog(accountid1, time1):
    v = models.AccountBlog.objects.filter(accountid=accountid1, time=time1)
    if len(v):
        models.AccountBlog.objects.filter(accountid=accountid1, time=time1).delete()
        return "deleteaccountblog"
    else:
        return "fail"

# 删除社团公告。如果删除成功，则返回字符串deleteclubblog
def delete_club_blog(name1, time1):
    v = models.ClubBlog.objects.filter(name=name1, time=time1)
    if len(v):
        models.ClubBlog.objects.filter(name=name1, time=time1).delete()
        return "deleteclubblog"
    else:
        return "fail"

# 删除签到活动。如果删除成功，则返回字符串deleteclubsign
def delete_club_sign(name1, time1):
    v = models.ClubSign.objects.filter(name=name1, time=time1)
    if len(v):
        models.ClubSign.objects.filter(name=name1, time=time1).delete()
        models.AccountSign.objects.filter(name=name1, time=time1).delete()
        return "deleteclubsign"
    else:
        return "fail"