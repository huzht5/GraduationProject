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
    if opcode=="login_with_password":
        accountid=request.POST.get('accountid','')
        password=request.POST.get('password','')
        response = login_with_password(accountid, password)
    elif opcode=="login_no_password":
        response = HttpResponse("succeed")
    elif opcode=="get_followed_clubs_data":
        accountid=request.POST.get('accountid','')
        response = get_followed_clubs_data(accountid)
    elif opcode=="get_advice_clubs_data":
        accountid=request.POST.get('accountid','')
        response = get_advice_clubs_data(accountid)
    elif opcode=="get_followed_accounts_data":
        accountid=request.POST.get('accountid','')
        response = get_followed_accounts_data(accountid)
    elif opcode=="get_advice_accounts_data":
        accountid=request.POST.get('accountid','')
        response = get_advice_accounts_data(accountid)
    elif opcode=="get_my_account_information":
        accountid=request.POST.get('accountid','')
        response = get_my_account_information(accountid)
    elif opcode=="get_my_clubs_data":
        accountid=request.POST.get('accountid','')
        response = get_my_clubs_data(accountid)
    elif opcode=="get_account_blog":
        accountid=request.POST.get('accountid','')
        response = get_account_blog(accountid)
    elif opcode=="get_club_blog":
        name=request.POST.get('name','')
        response = get_club_blog(name)
    elif opcode=="get_account_information":
        accountid=request.POST.get('accountid','')
        followerid=request.POST.get('followerid','')
        response = get_account_information(accountid, followerid)
    elif opcode=="get_both_clubs":
        accountid=request.POST.get('accountid','')
        followerid=request.POST.get('followerid','')
        response = get_both_clubs(followerid, accountid)
    elif opcode=="get_club_information":
        name=request.POST.get('name','')
        followerid=request.POST.get('followerid','')
        response = get_club_information(name, followerid)
    elif opcode=="get_followed_clubs_blog":
        accountid=request.POST.get('accountid','')
        response = get_followed_clubs_blog(accountid)
    elif opcode=="find_club":
        content=request.POST.get('mcontent','')
        response = find_club(content)
    elif opcode=="get_followed_accounts_blog":
        accountid=request.POST.get('accountid','')
        response = get_followed_accounts_blog(accountid)
    elif opcode=="find_account":
        content=request.POST.get('mcontent','')
        accountid=request.POST.get('accountid','')
        response = find_account(content, accountid)
    elif opcode=="get_club_sign":
        name=request.POST.get('name','')
        response = get_club_sign(name)
    elif opcode=="check_sign":
        name=request.POST.get('name','')
        time=request.POST.get('time','')
        response = check_sign(name, time)
    elif opcode=="get_message":
        accountid=request.POST.get('accountid','')
        response = get_message(accountid)
    else:
        response = "wrongopcode"
    return HttpResponse(response)


# 需要密码的登录。如果账号未注册，返回字符串unregistered；如果密码错误，则返回字符串wrongpassword；如果登录成功，则返回succeed
def login_with_password(accountid1, password1):
    try:
        v1 = models.Account.objects.get(accountid=accountid1)
        if v1.password != password1:
            return "wrongpassword"
        else:
            return "succeed"
    except:
        return "unregistered"

# 获取已关注的社团。参数accountid1是请求数据的用户的账号。返回json数据字符串
def get_followed_clubs_data(accountid1):
    v = models.Account.objects.filter(accountid=accountid1)
    if len(v):
        # list用于储存最终要返回的数据
        list=[]
        # 查找数据库中的ClubFollow表，找出此用户关注的所有社团，用temp遍历
        for temp in models.ClubFollow.objects.filter(followerid=accountid1):
            # 查找数据库中的Club表，找出每个社团的名字、社长的账号、社长的用户昵称，然后插入到list里
            v1 = models.Club.objects.filter(name=temp.name)
            temp_list = {
                'name': v1[0].name,
                'ownerid': v1[0].ownerid,
                'ownername': v1[0].ownername
            }
            list.append(temp_list)
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"

# 获取推荐的社团。参数accountid1是请求数据的用户的账号。返回json数据字符串
def get_advice_clubs_data(accountid1):
    v = models.Account.objects.filter(accountid=accountid1)
    if len(v):
        '''
    	list用于储存最终要返回的数据
	    clublist用于储存推荐社团并排序
    	followaccountlist用于储存已关注的用户
    	donotaddlist用于剔除不存入list的数据
	    num用于记录list中已存社团的数量
	    maxnum用于限制返回的数据的总数
	    '''
        list=[]
        clublist=[]
        followaccountlist=[]
        donotaddlist=[]
        num = 0
        maxnum = 9
        # 查找AccountFollow表，把此用户已关注的所有用户储存到followaccountlist里
        for temp in models.AccountFollow.objects.filter(followerid=accountid1):
            followaccountlist.append(temp.accountid)
        # 查找ClubFollow表，把此用户已关注的所有社团储存到donotaddlist里
        for temp1 in models.ClubFollow.objects.filter(followerid=accountid1):
            donotaddlist.append(temp1.name)
        # 把Club表按粉丝数从大到小排好序
        v1 = models.Club.objects.order_by('-count')
        # 如果followaccountlist的长度不为0，则优先推荐在此用户关注的用户中共同关注数最多的社团
        # 如果followaccountlist的长度为0，即此用户没有关注任何用户，则直接往list里插入前maxnum + 1个粉丝数最多的社团
        if len(followaccountlist):
            for i in followaccountlist:
                # 对于此用户A关注的每一个用户B，把用户B关注的所有社团插入到clublist中，如果clublist中已存在某个社团，则这个社团的like加一
                for temp2 in models.ClubFollow.objects.filter(followerid=i):
                    breakflag=False
                    for j in clublist:
                        if temp2.name==j[0][1]:
                            j[1][1] += 1
                            breakflag=True
                            break
                    if breakflag==False:
                        club = [['name', temp2.name], ['like', 1]]
                        clublist.append(club)
            # 如果clublist的长度不为0，则把clublist按like从大到小排序，之后把clublist中的社团插入到list中
            if len(clublist):
                clublist.sort(key=takelikeclub,reverse=True)
                for temp3 in clublist:
                    # 如果list中数据总数已足够，则不再插入数据
                    if num>maxnum:
                        break
                    # 使用breakflag1来剔除掉已存在donotaddlist里的社团
                    breakflag1 = False
                    for m in donotaddlist:
                        if temp3[0][1]==m:
                            breakflag1=True
                            break
                    # 把社团的名字、社长的账号、社长的用户昵称插入list里
                    if breakflag1==False:
                        v2 = models.Club.objects.filter(name=temp3[0][1])
                        temp_list = {
                            'name': v2[0].name,
                            'ownerid': v2[0].ownerid,
                            'ownername': v2[0].ownername
                        }
                        list.append(temp_list)
                        # 往list中插入一个数据后，也把此数据插入到donotaddlist中，然后把num加一
                        donotaddlist.append(temp3[0][1])
                        num += 1
        # 如果优先推荐的社团数量少于所需总数，则list中剩下的空位插入粉丝数最多的那些社团
        for temp4 in v1:
            # 如果list中数据总数已足够，则不再插入数据
            if num>maxnum:
                break
            # 使用breakflag2来剔除掉已存在donotaddlist里的社团
            breakflag2 = False
            for n in donotaddlist:
                if temp4.name==n:
                    breakflag2=True
                    break
            # 把社团的名字、社长的账号、社长的用户昵称插入list里
            if breakflag2==False:
                temp_list = {
                    'name': temp4.name,
                    'ownerid': temp4.ownerid,
                    'ownername': temp4.ownername
                }
                list.append(temp_list)
                # 往list中插入一个数据后，把num加一
                num += 1
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"
	
# 辅助函数。用于在获取推荐社团时按like从大到小排序
def takelikeclub(club):
    return club[1][1]

# 获取已关注的用户。参数accountid1是请求数据的用户的账号。返回json数据字符串
def get_followed_accounts_data(accountid1):
    v = models.Account.objects.filter(accountid=accountid1)
    if len(v):
        # list用于储存最终要返回的数据
        list=[]
        # followclublist用于储存此用户已关注的社团
        followclublist=[]
        # 查找ClubFollow表，把此用户已关注的所有社团插入到followclublist中
        for temp in models.ClubFollow.objects.filter(followerid=accountid1):
            followclublist.append(temp.name)
        # 查找AccountFollow表，获得此用户关注的所有用户
        for temp1 in models.AccountFollow.objects.filter(followerid=accountid1):
            v1 = models.Account.objects.filter(accountid=temp1.accountid)
            # 对于此用户A关注的每一个用户B，计算它们共同关注的社团数量，并储存到like中
            like = 0
            for temp2 in followclublist:
                v2 = models.ClubFollow.objects.filter(name=temp2,followerid=temp1.accountid)
                if len(v2):
                    like += 1
            # 把用户的账号、用户昵称、共同关注社团数插入list里
            temp_list = {
                'id': v1[0].accountid,
                'name': v1[0].name,
                'like': like
            }
            list.append(temp_list)
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"
	
# 获取推荐的用户。参数accountid1是请求数据的用户的账号。返回json数据字符串
def get_advice_accounts_data(accountid1):
    v = models.Account.objects.filter(accountid=accountid1)
    if len(v):
        '''
	    list用于储存最终要返回的数据
	    accountlist用于储存推荐用户并排序
    	followclublist用于储存已关注的社团
	    donotaddlist用于剔除不存入list的数据
	    num用于记录list中已存用户的数量
	    maxnum用于限制返回的数据的总数
	    '''
        list=[]
        accountlist=[]
        followclublist=[]
        donotaddlist=[]
        num = 0
        maxnum = 9
        # 查找ClubFollow表，把此用户已关注的所有社团储存到followclublist里
        for temp in models.ClubFollow.objects.filter(followerid=accountid1):
            followclublist.append(temp.name)
        # 查找AccountFollow表，把此用户已关注的所有用户储存到donotaddlist里
        for temp1 in models.AccountFollow.objects.filter(followerid=accountid1):
            donotaddlist.append(temp1.accountid)
        # 把Account表按粉丝数从大到小排好序
        v1 = models.Account.objects.order_by('-count')
        # 如果followclublist的长度不为0，则优先推荐与此用户共同关注的社团数最多的用户
        # 如果followclublist的长度为0，即此用户没有关注任何社团，则直接往list里插入前maxnum + 1个粉丝数最多的用户
        if len(followclublist):
            for i in followclublist:
                # 对于此用户关注的每一个社团，把关注这个社团的所有用户插入到accountlist中，如果accountlist中已存在某个用户，则这个用户的like加一
                for temp2 in models.ClubFollow.objects.filter(name=i):
                    # 使用breakflag来剔除掉此用户本身
                    breakflag=False
                    if accountid1==temp2.followerid:
                        breakflag=True
                    for j in accountlist:
                        if temp2.followerid==j[0][1]:
                            j[1][1] += 1
                            breakflag=True
                            break
                    if breakflag==False:
                        person = [['id', temp2.followerid], ['like', 1]]
                        accountlist.append(person)
            # 如果accountlist的长度不为0，则把accountlist按like从大到小排序，之后把accountlist中的用户插入到list中
            if len(accountlist):
                accountlist.sort(key=takelikeperson,reverse=True)
                for temp3 in accountlist:
                    # 如果list中数据总数已足够，则不再插入数据
                    if num>maxnum:
                        break	
                    # 使用breakflag1来剔除掉已存在donotaddlist里的用户
                    breakflag1 = False
                    for m in donotaddlist:
                        if temp3[0][1]==m:
                            breakflag1=True
                            break
                    if breakflag1==False:
                        v2 = models.Account.objects.filter(accountid=temp3[0][1])
                        # 把用户的账号、用户昵称、共同关注社团数插入list里
                        temp_list = {
                            'id': v2[0].accountid,
                            'name': v2[0].name,
                            'like': temp3[1][1]
                        }
                        list.append(temp_list)
                        # 往list中插入一个数据后，也把此数据插入到donotaddlist中，然后把num加一
                        donotaddlist.append(temp3[0][1])
                        num += 1
        # 如果优先推荐的用户数量少于所需总数，则list中剩下的空位插入粉丝数最多的那些用户
        for temp4 in v1:
            # 如果list中数据总数已足够，则不再插入数据
            if num>maxnum:
                break
            # 使用breakflag2来剔除掉已存在donotaddlist里的用户和此用户本身
            breakflag2 = False
            if accountid1==temp4.accountid:
                breakflag2=True
            for n in donotaddlist:
                if temp4.accountid==n:
                    breakflag2=True
                    break
            # 把用户的账号、用户昵称、共同关注社团数插入list里
            if breakflag2==False:
                temp_list = {
                    'id': temp4.accountid,
                    'name': temp4.name,
                    'like': 0
                }
                list.append(temp_list)
                # 往list中插入一个数据后，把num加一
                num += 1
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"
	
# 辅助函数。用于在获取推荐用户时按like从大到小排序
def takelikeperson(person):
    return person[1][1]

# 获取我的账号信息。参数accountid1是请求数据的用户的账号。返回字符串str
def get_my_account_information(accountid1):
    v = models.Account.objects.filter(accountid=accountid1)
    if len(v):
        # str由两个字符串：用户昵称和个人简介组合而成，用/分隔
        v1 = models.Account.objects.filter(accountid=accountid1)
        str = v1[0].name+"/"+v1[0].information
        return str
    else:
        return "fail"

# 获取我的社团。参数accountid1是请求数据的用户的账号。返回json数据字符串
def get_my_clubs_data(accountid1):
    v = models.Account.objects.filter(accountid=accountid1)
    if len(v):
        # list用于储存最终要返回的数据
        list=[]
        # 查找Club表，找出所有社长账号是此用户的账号的社团
        for temp in models.Club.objects.filter(ownerid=accountid1):
            # 把社团的名字、社长的账号、社长的用户昵称插入list里
            temp_list = {
                'name': temp.name,
                'ownerid': temp.ownerid,
                'ownername': temp.ownername
            }
            list.append(temp_list)
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"

# 获取用户动态。参数accountid1是要获取动态的用户的账号。返回json数据字符串
def get_account_blog(accountid1):
    v = models.Account.objects.filter(accountid=accountid1)
    if len(v):
        # list用于储存最终要返回的数据
        list=[]
        v1 = models.Account.objects.filter(accountid=accountid1)
        v2 = models.AccountBlog.objects.all()
        for temp in v2:
            # 把用户昵称、用户的账号、用户动态的时间，用户动态的内容插入list里
            if temp.accountid==accountid1:
                temp_list = {
                    'name': v1[0].name,
                    'id': accountid1,
                    'time': temp.time,
                    'message': temp.message
                }
                list.append(temp_list)
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"

# 获取社团公告。返回json数据字符串
def get_club_blog(name1):
    v = models.Club.objects.filter(name=name1)
    if len(v):
        # list用于储存最终要返回的数据
        list=[]
        v1 = models.ClubBlog.objects.all()
        v2 = models.Club.objects.filter(name=name1)
        for temp in v1:
            # 把社团的名字、公告的时间、公告的内容、社长的用户昵称和社长的账号插入list里
            if temp.name==name1:
                temp_list = {
                    'name': name1,
                    'time': temp.time,
                    'message': temp.message,
                    'ownername': v2[0].ownername,
                    'ownerid': v2[0].ownerid
                }
                list.append(temp_list)
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"

# 获取用户信息。参数accountid1是对方用户的账号，参数followerid1是请求数据的用户的账号。返回字符串str
def get_account_information(accountid1,followerid1):
    v = models.Account.objects.filter(accountid=followerid1)
    v1 = models.Account.objects.filter(accountid=accountid1)
    if len(v) and len(v1):
        # str由三个字符串：用户昵称、个人简介和是否已关注标志组合而成，用/分隔
        v2 = models.AccountFollow.objects.filter(accountid=accountid1,followerid=followerid1)
        if len(v2):
            str = v1[0].name+"/"+v1[0].information+"/1"
            return str
        else:
            str = v1[0].name+"/"+v1[0].information+"/0"
            return str
    else:
        return "fail"

# 获取用户动态。参数accountid1是请求数据的用户的账号，参数accountid2是对方用户的账号。返回json数据字符串
def get_both_clubs(accountid1,accountid2):
    v = models.Account.objects.filter(accountid=accountid1)
    v2 = models.Account.objects.filter(accountid=accountid2)
    if len(v) and len(v2):
        # list用于储存最终要返回的数据
        list=[]
        # mylist用于储存此用户已关注的社团
        mylist=[]
        # 查找ClubFollow表，把此用户已关注的所有社团储存到mylist里
        for temp in models.ClubFollow.objects.filter(followerid=accountid1):
            mylist.append(temp.name)
        # 查找ClubFollow表，对于对方用户已关注的每个社团，如果已存在mylist里，则表示共同关注，插入到list里
        for temp1 in models.ClubFollow.objects.filter(followerid=accountid2):
            for temp2 in mylist:
                if temp1.name==temp2:
                    v1 = models.Club.objects.filter(name=temp2)
                    # 把社团的名字、社长的账号、社长的用户昵称插入list里
                    temp_list = {
                        'name': v1[0].name,
                        'ownerid': v1[0].ownerid,
                        'ownername': v1[0].ownername
                    }
                    list.append(temp_list)
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"

# 获取社团信息。返回字符串str
def get_club_information(name1, followerid1):
    v = models.Account.objects.filter(accountid=followerid1)
    v1 = models.Club.objects.filter(name=name1)
    if len(v) and len(v1):
        v2 = models.ClubFollow.objects.filter(name=name1,followerid=followerid1)
        follow = "0"
        if len(v2):
            follow = "1"
        # str由两个字符串：社团简介和是否已关注标志组合而成，用/分隔
        str = v1[0].information+"/"+follow
        return str
    else:
        return "fail"

# 获取已关注的社团的公告。参数accountid1是请求数据的用户的账号。返回json数据字符串
def get_followed_clubs_blog(accountid1):
    v = models.Account.objects.filter(accountid=accountid1)
    if len(v):
        '''
	    list用于储存最终要返回的数据
	    mylist用于储存已关注的社团
	    num用于记录list中已存公告的数量
	    maxnum用于限制返回的数据的总数
	    '''
        list=[]
        mylist=[]
        num = 0
        maxnum = 19
        # 查找ClubFollow表，把此用户已关注的所有社团储存到mylist里
        for temp in models.ClubFollow.objects.filter(followerid=accountid1):
            mylist.append(temp.name)
        v1 = models.ClubBlog.objects.all()
        for temp1 in v1:
            # 如果list中数据总数已足够，则不再插入数据
            if num>maxnum:
                break
            for temp2 in mylist:
                if temp1.name==temp2:
                    v2 = models.Club.objects.filter(name=temp2)
                    # 把社团的名字、公告的时间、公告的内容、社长的用户昵称和社长的账号插入list里
                    temp_list = {
                        'name': temp1.name,
                        'time': temp1.time,
                        'message': temp1.message,
                        'ownername': v2[0].ownername,
                        'ownerid': v2[0].ownerid
                    }
                    list.append(temp_list)
                    # 往list中插入一个数据后，把num加一
                    num += 1
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"

# 搜索社团。返回json数据字符串
def find_club(content1):
    # list用于储存最终要返回的数据
    list=[]
    v1 = models.Club.objects.all()
    # 如果社团名或者社长的用户昵称包含搜索内容，则把这个社团插入list
    for temp in v1:
        breakflag = False
        if content1 in temp.name:
            breakflag = True
        if content1 in temp.ownername:
            breakflag = True
        if breakflag == True:
            # 把社团的名字、社长的账号、社长的用户昵称插入list里
            temp_list = {
                'name': temp.name,
                'ownerid': temp.ownerid,
                'ownername': temp.ownername
            }
            list.append(temp_list)
    # 把list中的数据转换为json数据字符串，然后返回
    s = json.dumps(list, ensure_ascii=False)
    return s

# 获取已关注的用户的动态。参数accountid1是请求数据的用户的账号。返回json数据字符串
def get_followed_accounts_blog(accountid1):
    v = models.Account.objects.filter(accountid=accountid1)
    if len(v):
        '''
    	list用于储存最终要返回的数据
	    mylist用于储存已关注的用户
	    num用于记录list中已存动态的数量
	    maxnum用于限制返回的数据的总数
	    '''
        list=[]
        mylist=[]
        num = 0
        maxnum = 19
        # 查找AccountFollow表，把此用户已关注的所有用户储存到mylist里
        for temp in models.AccountFollow.objects.filter(followerid=accountid1):
            mylist.append(temp.accountid)
        v1 = models.AccountBlog.objects.all()
        for temp1 in v1:
            # 如果list中数据总数已足够，则不再插入数据
            if num>maxnum:
                break
            for temp2 in mylist:
                if temp1.accountid==temp2:
                    v2 = models.Account.objects.filter(accountid=temp2)
                    # 把用户昵称、用户的账号、用户动态的时间，用户动态的内容插入list里
                    temp_list = {
                        'name': v2[0].name,
                        'id': temp2,
                        'time': temp1.time,
                        'message': temp1.message
                    }
                    list.append(temp_list)
                    # 往list中插入一个数据后，把num加一
                    num += 1
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"

# 搜索用户。返回json数据字符串
def find_account(content1, accountid1):
    v = models.Account.objects.filter(accountid=accountid1)
    if len(v):
        # list用于储存最终要返回的数据
        list=[]
        # mylist用于储存已关注的社团
        mylist=[]
        for temp in models.ClubFollow.objects.filter(followerid=accountid1):
            mylist.append(temp.name)
        v1 = models.Account.objects.all()
        # 如果账号或者用户昵称包含搜索内容，则把这个用户插入list
        for temp1 in v1:
            breakflag = False
            if content1 in temp1.accountid:
                breakflag = True
            if content1 in temp1.name:
                breakflag = True
            if breakflag == True:
                # 计算共同关注的社团数，用like储存
                like = 0
                for temp2 in mylist:
                    v2 = models.ClubFollow.objects.filter(name=temp2, followerid=temp1.accountid)
                    if len(v2):
                        like += 1
                # 把用户的账号、用户昵称、共同关注社团数插入list里
                temp_list = {
                    'id': temp1.accountid,
                    'name': temp1.name,
                    'like': like
                }
                list.append(temp_list)
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"

# 获取社团签到活动。返回json数据字符串
def get_club_sign(name1):
    v = models.Club.objects.filter(name=name1)
    if len(v):
        # list用于储存最终要返回的数据
        list=[]
        v1 = models.ClubSign.objects.all()
        for temp in v1:
            # 把社团的名字、签到活动的时间、签到活动的内容和签到活动的状态插入list里
            if temp.name==name1:
                temp_list = {
                    'name': name1,
                    'time': temp.time,
                    'message': temp.message,
                    'state': temp.state
                }
                list.append(temp_list)
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"

# 获取签到人员。返回json数据字符串
def check_sign(name1, time1):
    v = models.Club.objects.filter(name=name1)
    v1 = models.ClubSign.objects.filter(name=name1, time=time1)
    if len(v) and len(v1):
        # list用于储存最终要返回的数据
        list=[]
        v1 = models.AccountSign.objects.filter(name=name1, time=time1)
        for temp in v1:
            v2 = models.Account.objects.filter(accountid=temp.accountid)
            # 把社团的名字、签到活动的时间、签到活动的内容和签到活动的状态插入list里
            temp_list = {
                'id': temp.accountid,
                'name': v2[0].name,
                'time': temp.signtime
            }
            list.append(temp_list)
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"

# 获取消息。参数accountid1是请求数据的用户的账号。返回json数据字符串
def get_message(accountid1):
    v = models.Account.objects.filter(accountid=accountid1)
    if len(v):
        # list用于储存最终要返回的数据
        list=[]
        # 查找数据库中的AccountMessage表，找出此用户收到的所有消息，用temp遍历
        for temp in models.AccountMessage.objects.filter(receiverid=accountid1):
            # 查找数据库中的Account表，把每个消息发送人的账号、用户昵称、发送时间和消息内容插入到list里
            v1 = models.Account.objects.filter(accountid=temp.senderid)
            temp_list = {
                'name': v1[0].name,
                'id': temp.senderid,
                'time': temp.time,
                'message': temp.message,
                'state': "1"
            }
            list.append(temp_list)
        models.AccountMessage.objects.filter(receiverid=accountid1).delete()
        # 把list中的数据转换为json数据字符串，然后返回
        s = json.dumps(list, ensure_ascii=False)
        return s
    else:
        return "fail"