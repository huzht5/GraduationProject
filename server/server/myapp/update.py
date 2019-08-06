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
    if opcode=="change_account_information":
        accountid=request.POST.get('accountid','')
        name=request.POST.get('name','')
        information=request.POST.get('information','')
        response = change_account_information(accountid, name, information)
    elif opcode=="change_club_information":
        name=request.POST.get('name','')
        information=request.POST.get('information','')
        response = change_club_information(name, information)
    elif opcode=="stop_club_sign":
        name=request.POST.get('name','')
        time=request.POST.get('time','')
        response = stop_club_sign(name, time)
    else:
        response = "wrongopcode"
    return HttpResponse(response)


# 修改个人信息。如果修改成功，则返回字符串changeaccountinformation
def change_account_information(accountid1, name1, information1):
    v1 = models.Account.objects.filter(accountid=accountid1)
    if len(v1):
        models.Account.objects.filter(accountid=accountid1).update(name=name1,information=information1)
        models.Club.objects.filter(ownerid=accountid1).update(ownername=name1)
        return "changeaccountinformation"
    else:
        return "fail"

# 修改社团简介。如果修改成功，则返回字符串changeclubinformation
def change_club_information(name1, information1):
    v1 = models.Club.objects.filter(name=name1)
    if len(v1):
        models.Club.objects.filter(name=name1).update(information=information1)
        return "changeclubinformation"
    else:
        return "fail"

# 结束签到活动。如果结束成功，则返回字符串stopclubsign
def stop_club_sign(name1, time1):
    v1 = models.ClubSign.objects.filter(name=name1,time=time1)
    if len(v1):
        models.ClubSign.objects.filter(name=name1,time=time1).update(state="签到结束")
        return "stopclubsign"
    else:
        return "fail"