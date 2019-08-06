from django.test import Client,TestCase
from django.http import HttpResponse
from myapp import models

class QueryInterfaceTestCase(TestCase):
    def setUp(self):
        super(QueryInterfaceTestCase, self).setUp()
        self.client = Client(enforce_csrf_checks=True)
        # 初始化时，往Account表中插入两个用户、往Club表中插入一个社团
        # 往ClubFollow表中插入一个关注信息\往AccountFollow表中插入一个关注信息
        # 往AccountBlog表中插入一个用户动态、往ClubBlog表中插入一个社团公告
        # 往ClubSign表中插入一个社团签到活动、往AccountSign表中插入一个签到信息、往AccountMessage表中插入一个聊天消息
        models.Account.objects.create(accountid="1", password="1", name="1", information="1", count="0")
        models.Account.objects.create(accountid="2", password="2", name="2", information="2", count="1")
        models.Account.objects.create(accountid="3", password="3", name="3", information="3", count="0")
        models.Club.objects.create(name="动漫社", ownerid="1", ownername="1", information="1", count="1")
        models.Club.objects.create(name="篮球社", ownerid="2", ownername="2", information="2", count="1")
        models.ClubFollow.objects.create(name="动漫社", followerid="1")
        models.AccountFollow.objects.create(accountid="2", followerid="1")
        models.AccountBlog.objects.create(accountid="1", time="2019-04-04  20:13:24", message="1")
        models.AccountBlog.objects.create(accountid="2", time="2019-04-04  20:13:24", message="2")
        models.ClubBlog.objects.create(name="动漫社", time="2019-04-04  20:13:24", message="1")
        models.ClubSign.objects.create(name="动漫社", time="2019-04-04  20:13:24", message="1", state="可签到")
        models.AccountSign.objects.create(accountid="1", name="动漫社", time="2019-04-04  20:13:24", signtime="2019-04-04  20:15:24")
        models.AccountMessage.objects.create(senderid="2", receiverid="1", time="2019-04-04  20:13:24", message="1")

    def test_login_with_password(self):
        # 测试正常登录、登录不存在的账号与使用错误密码登录
        response = self.client.post('/query/login_with_password/', {'accountid':'1','password':'1'})
        response1 = self.client.post('/query/login_with_password/', {'accountid':'4','password':'4'})
        response2 = self.client.post('/query/login_with_password/', {'accountid':'1','password':'2'})
        self.assertEqual(response.content, b"succeed", 'test login_with_password fail')
        self.assertEqual(response1.content, b"unregistered", 'test login_with_password fail')
        self.assertEqual(response2.content, b"wrongpassword", 'test login_with_password fail')

    def test_login_no_password(self):
        # 测试正常登录、登录不存在的账号与使用错误密码登录
        response = self.client.post('/query/login_no_password/')
        self.assertEqual(response.content, b"succeed", 'test login_no_password fail')

    def test_get_followed_clubs_data(self):
        # 测试正常获取已关注的社团与使用不存在的用户获取
        response = self.client.post('/query/get_followed_clubs_data/', {'accountid':'1'})
        response1 = self.client.post('/query/get_followed_clubs_data/', {'accountid':'4'})
        self.assertEqual(response.content, HttpResponse('[{"name": "动漫社", "ownerid": "1", "ownername": "1"}]').content, 'test get_followed_clubs_data fail')
        self.assertEqual(response1.content, b"fail", 'test get_followed_clubs_data fail')

    def test_get_advice_clubs_data(self):
        # 测试正常获取推荐社团与使用不存在的用户获取
        response = self.client.post('/query/get_advice_clubs_data/', {'accountid':'1'})
        response1 = self.client.post('/query/get_advice_clubs_data/', {'accountid':'4'})
        self.assertEqual(response.content, HttpResponse('[{"name": "篮球社", "ownerid": "2", "ownername": "2"}]').content, 'test get_advice_clubs_data fail')
        self.assertEqual(response1.content, b"fail", 'test get_advice_clubs_data fail')

    def test_get_followed_accounts_data(self):
        # 测试正常获取已关注的用户与使用不存在的用户获取
        response = self.client.post('/query/get_followed_accounts_data/', {'accountid':'1'})
        response1 = self.client.post('/query/get_followed_accounts_data/', {'accountid':'4'})
        self.assertEqual(response.content, HttpResponse('[{"id": "2", "name": "2", "like": 0}]').content, 'test get_followed_accounts_data fail')
        self.assertEqual(response1.content, b"fail", 'test get_followed_accounts_data fail')

    def test_get_advice_accounts_data(self):
        # 测试正常获取推荐用户与使用不存在的用户获取
        response = self.client.post('/query/get_advice_accounts_data/', {'accountid':'1'})
        response1 = self.client.post('/query/get_advice_accounts_data/', {'accountid':'4'})
        self.assertEqual(response.content, HttpResponse('[{"id": "3", "name": "3", "like": 0}]').content, 'test get_advice_accounts_data fail')
        self.assertEqual(response1.content, b"fail", 'test get_advice_accounts_data fail')

    def test_get_my_account_information(self):
        # 测试正常获取用户自身信息与使用不存在的用户获取
        response = self.client.post('/query/get_my_account_information/', {'accountid':'1'})
        response1 = self.client.post('/query/get_my_account_information/', {'accountid':'4'})
        self.assertEqual(response.content, b"1/1", 'test get_my_account_information fail')
        self.assertEqual(response1.content, b"fail", 'test get_my_account_information fail')

    def test_get_my_clubs_data(self):
        # 测试正常获取用户创建的社团与使用不存在的用户获取
        response = self.client.post('/query/get_my_clubs_data/', {'accountid':'1'})
        response1 = self.client.post('/query/get_my_clubs_data/', {'accountid':'4'})
        self.assertEqual(response.content, HttpResponse('[{"name": "动漫社", "ownerid": "1", "ownername": "1"}]').content, 'test get_my_clubs_data fail')
        self.assertEqual(response1.content, b"fail", 'test get_my_clubs_data fail')

    def test_get_account_blog(self):
        # 测试正常获取用户的个人动态与使用不存在的用户获取
        response = self.client.post('/query/get_account_blog/', {'accountid':'1'})
        response1 = self.client.post('/query/get_account_blog/', {'accountid':'4'})
        self.assertEqual(response.content, HttpResponse('[{"name": "1", "id": "1", "time": "2019-04-04  20:13:24", "message": "1"}]').content, 'test get_account_blog fail')
        self.assertEqual(response1.content, b"fail", 'test get_account_blog fail')

    def test_get_club_blog(self):
        # 测试正常获取社团公告与使用不存在的社团获取
        response = self.client.post('/query/get_club_blog/', {'name':'动漫社'})
        response1 = self.client.post('/query/get_club_blog/', {'name':'网球社'})
        self.assertEqual(response.content, HttpResponse('[{"name": "动漫社", "time": "2019-04-04  20:13:24", "message": "1", "ownername": "1", "ownerid": "1"}]').content, 'test get_club_blog fail')
        self.assertEqual(response1.content, b"fail", 'test get_club_blog fail')

    def test_get_account_information(self):
        # 测试正常获取用户信息与使用不存在的用户获取
        response = self.client.post('/query/get_account_information/', {'accountid':'2','followerid':'1'})
        response1 = self.client.post('/query/get_account_information/', {'accountid':'4','followerid':'1'})
        self.assertEqual(response.content, b"2/2/1", 'test get_account_information fail')
        self.assertEqual(response1.content, b"fail", 'test get_account_information fail')

    def test_get_both_clubs(self):
        # 测试正常获取共同关注的社团与使用不存在的用户获取
        response = self.client.post('/query/get_both_clubs/', {'accountid':'2','followerid':'1'})
        response1 = self.client.post('/query/get_both_clubs/', {'accountid':'4','followerid':'1'})
        self.assertEqual(response.content, HttpResponse('[]').content, 'test get_both_clubs fail')
        self.assertEqual(response1.content, b"fail", 'test get_both_clubs fail')

    def test_get_club_information(self):
        # 测试正常获取社团信息与使用不存在的社团获取
        response = self.client.post('/query/get_club_information/', {'name':'动漫社','followerid':'1'})
        response1 = self.client.post('/query/get_club_information/', {'accountid':'网球社','followerid':'1'})
        self.assertEqual(response.content, b"1/1", 'test get_club_information fail')
        self.assertEqual(response1.content, b"fail", 'test get_club_information fail')

    def test_get_followed_clubs_blog(self):
        # 测试正常获取已关注的社团公告与使用不存在的用户获取
        response = self.client.post('/query/get_followed_clubs_blog/', {'accountid':'1'})
        response1 = self.client.post('/query/get_followed_clubs_blog/', {'accountid':'4'})
        self.assertEqual(response.content, HttpResponse('[{"name": "动漫社", "time": "2019-04-04  20:13:24", "message": "1", "ownername": "1", "ownerid": "1"}]').content, 'test get_followed_clubs_blog fail')
        self.assertEqual(response1.content, b"fail", 'test get_followed_clubs_blog fail')

    def test_find_club(self):
        # 测试搜索社团
        response = self.client.post('/query/find_club/', {'mcontent':'1'})
        response1 = self.client.post('/query/find_club/', {'mcontent':'4'})
        self.assertEqual(response.content, HttpResponse('[{"name": "动漫社", "ownerid": "1", "ownername": "1"}]').content, 'test find_club fail')
        self.assertEqual(response1.content, HttpResponse('[]').content, 'test find_club fail')

    def test_get_followed_accounts_blog(self):
        # 测试正常获取已关注的用户动态与使用不存在的用户获取
        response = self.client.post('/query/get_followed_accounts_blog/', {'accountid':'1'})
        response1 = self.client.post('/query/get_followed_accounts_blog/', {'accountid':'4'})
        self.assertEqual(response.content, HttpResponse('[{"name": "2", "id": "2", "time": "2019-04-04  20:13:24", "message": "2"}]').content, 'test get_followed_accounts_blog fail')
        self.assertEqual(response1.content, b"fail", 'test get_followed_accounts_blog fail')

    def test_find_account(self):
        # 测试搜索社团
        response = self.client.post('/query/find_account/', {'mcontent':'2','accountid':'1'})
        response1 = self.client.post('/query/find_account/', {'mcontent':'4','accountid':'1'})
        self.assertEqual(response.content, HttpResponse('[{"id": "2", "name": "2", "like": 0}]').content, 'test find_account fail')
        self.assertEqual(response1.content, HttpResponse('[]').content, 'test find_account fail')

    def test_get_club_sign(self):
        # 测试正常获取社团签到活动与获取不存在的社团的签到活动
        response = self.client.post('/query/get_club_sign/', {'name':'动漫社'})
        response1 = self.client.post('/query/get_club_sign/', {'name':'网球社'})
        self.assertEqual(response.content, HttpResponse('[{"name": "动漫社", "time": "2019-04-04  20:13:24", "message": "1", "state": "可签到"}]').content, 'test get_club_sign fail')
        self.assertEqual(response1.content, b"fail", 'test get_club_sign fail')

    def test_check_sign(self):
        # 测试正常获取签到人员与获取不存在的签到活动的签到人员
        response = self.client.post('/query/check_sign/', {'name':'动漫社','time':'2019-04-04  20:13:24'})
        response1 = self.client.post('/query/check_sign/', {'name':'网球社','time':'2019-04-04  20:13:24'})
        self.assertEqual(response.content, HttpResponse('[{"id": "1", "name": "1", "time": "2019-04-04  20:15:24"}]').content, 'test check_sign fail')
        self.assertEqual(response1.content, b"fail", 'test check_sign fail')

    def test_get_message(self):
        # 测试正常获取聊天消息与获取不存在的用户的聊天消息
        response = self.client.post('/query/get_message/', {'accountid':'1'})
        response1 = self.client.post('/query/get_message/', {'accountid':'4'})
        self.assertEqual(response.content, HttpResponse('[{"name": "2", "id": "2", "time": "2019-04-04  20:13:24", "message": "1", "state": "1"}]').content, 'test get_message fail')
        self.assertEqual(response1.content, b"fail", 'test get_message fail')