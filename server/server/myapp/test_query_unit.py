from django.test import TestCase
from myapp import models
from myapp import query

class QueryUnitTestCase(TestCase):
    def setUp(self):
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
        response = query.login_with_password("1", "1")
        response1 = query.login_with_password("4", "4")
        response2 = query.login_with_password("1", "2")
        self.assertEqual(response, "succeed", 'test login_with_password fail')
        self.assertEqual(response1, "unregistered", 'test login_with_password fail')
        self.assertEqual(response2, "wrongpassword", 'test login_with_password fail')

    def test_get_followed_clubs_data(self):
        # 测试正常获取已关注的社团与使用不存在的用户获取
        response = query.get_followed_clubs_data("1")
        response1 = query.get_followed_clubs_data("4")
        self.assertEqual(response, '[{"name": "动漫社", "ownerid": "1", "ownername": "1"}]', 'test get_followed_clubs_data fail')
        self.assertEqual(response1, "fail", 'test get_followed_clubs_data fail')

    def test_get_advice_clubs_data(self):
        # 测试正常获取推荐社团与使用不存在的用户获取
        response = query.get_advice_clubs_data("1")
        response1 = query.get_advice_clubs_data("4")
        self.assertEqual(response, '[{"name": "篮球社", "ownerid": "2", "ownername": "2"}]', 'test get_advice_clubs_data fail')
        self.assertEqual(response1, "fail", 'test get_advice_clubs_data fail')

    def test_get_followed_accounts_data(self):
        # 测试正常获取已关注的用户与使用不存在的用户获取
        response = query.get_followed_accounts_data("1")
        response1 = query.get_followed_accounts_data("4")
        self.assertEqual(response, '[{"id": "2", "name": "2", "like": 0}]', 'test get_followed_accounts_data fail')
        self.assertEqual(response1, "fail", 'test get_followed_accounts_data fail')

    def test_get_advice_accounts_data(self):
        # 测试正常获取推荐用户与使用不存在的用户获取
        response = query.get_advice_accounts_data("1")
        response1 = query.get_advice_accounts_data("4")
        self.assertEqual(response, '[{"id": "3", "name": "3", "like": 0}]', 'test get_advice_accounts_data fail')
        self.assertEqual(response1, "fail", 'test get_advice_accounts_data fail')

    def test_get_my_account_information(self):
        # 测试正常获取用户自身信息与使用不存在的用户获取
        response = query.get_my_account_information("1")
        response1 = query.get_my_account_information("4")
        self.assertEqual(response, "1/1", 'test get_my_account_information fail')
        self.assertEqual(response1, "fail", 'test get_my_account_information fail')

    def test_get_my_clubs_data(self):
        # 测试正常获取用户创建的社团与使用不存在的用户获取
        response = query.get_my_clubs_data("1")
        response1 = query.get_my_clubs_data("4")
        self.assertEqual(response, '[{"name": "动漫社", "ownerid": "1", "ownername": "1"}]', 'test get_my_clubs_data fail')
        self.assertEqual(response1, "fail", 'test get_my_clubs_data fail')

    def test_get_account_blog(self):
        # 测试正常获取用户的个人动态与使用不存在的用户获取
        response = query.get_account_blog("1")
        response1 = query.get_account_blog("4")
        self.assertEqual(response, '[{"name": "1", "id": "1", "time": "2019-04-04  20:13:24", "message": "1"}]', 'test get_account_blog fail')
        self.assertEqual(response1, "fail", 'test get_account_blog fail')

    def test_get_club_blog(self):
        # 测试正常获取社团公告与使用不存在的社团获取
        response = query.get_club_blog("动漫社")
        response1 = query.get_club_blog("网球社")
        self.assertEqual(response, '[{"name": "动漫社", "time": "2019-04-04  20:13:24", "message": "1", "ownername": "1", "ownerid": "1"}]', 'test get_club_blog fail')
        self.assertEqual(response1, "fail", 'test get_club_blog fail')

    def test_get_account_information(self):
        # 测试正常获取用户信息与使用不存在的用户获取
        response = query.get_account_information("2", "1")
        response1 = query.get_account_information("4", "1")
        self.assertEqual(response, "2/2/1", 'test get_account_information fail')
        self.assertEqual(response1, "fail", 'test get_account_information fail')

    def test_get_both_clubs(self):
        # 测试正常获取共同关注的社团与使用不存在的用户获取
        response = query.get_both_clubs("1", "2")
        response1 = query.get_both_clubs("1", "4")
        self.assertEqual(response, '[]', 'test get_both_clubs fail')
        self.assertEqual(response1, "fail", 'test get_both_clubs fail')

    def test_get_club_information(self):
        # 测试正常获取社团信息与使用不存在的社团获取
        response = query.get_club_information("动漫社", "1")
        response1 = query.get_club_information("网球社", "1")
        self.assertEqual(response, "1/1", 'test get_club_information fail')
        self.assertEqual(response1, "fail", 'test get_club_information fail')

    def test_get_followed_clubs_blog(self):
        # 测试正常获取已关注的社团公告与使用不存在的用户获取
        response = query.get_followed_clubs_blog("1")
        response1 = query.get_followed_clubs_blog("4")
        self.assertEqual(response, '[{"name": "动漫社", "time": "2019-04-04  20:13:24", "message": "1", "ownername": "1", "ownerid": "1"}]', 'test get_followed_clubs_blog fail')
        self.assertEqual(response1, "fail", 'test get_followed_clubs_blog fail')

    def test_get_followed_accounts_blog(self):
        # 测试正常获取已关注的用户动态与使用不存在的用户获取
        response = query.get_followed_accounts_blog("1")
        response1 = query.get_followed_accounts_blog("4")
        self.assertEqual(response, '[{"name": "2", "id": "2", "time": "2019-04-04  20:13:24", "message": "2"}]', 'test get_followed_accounts_blog fail')
        self.assertEqual(response1, "fail", 'test get_followed_accounts_blog fail')

    def test_get_club_sign(self):
        # 测试正常获取社团签到活动与获取不存在的社团的签到活动
        response = query.get_club_sign("动漫社")
        response1 = query.get_club_sign("网球社")
        self.assertEqual(response, '[{"name": "动漫社", "time": "2019-04-04  20:13:24", "message": "1", "state": "可签到"}]', 'test get_club_sign fail')
        self.assertEqual(response1, "fail", 'test get_club_sign fail')

    def test_check_sign(self):
        # 测试正常获取签到人员与获取不存在的签到活动的签到人员
        response = query.check_sign("动漫社", "2019-04-04  20:13:24")
        response1 = query.check_sign("网球社", "2019-04-04  20:13:24")
        self.assertEqual(response, '[{"id": "1", "name": "1", "time": "2019-04-04  20:15:24"}]', 'test check_sign fail')
        self.assertEqual(response1, "fail", 'test check_sign fail')

    def test_get_message(self):
        # 测试正常获取聊天消息与获取不存在的用户的聊天消息
        response = query.get_message("1")
        response1 = query.get_message("4")
        self.assertEqual(response, '[{"name": "2", "id": "2", "time": "2019-04-04  20:13:24", "message": "1", "state": "1"}]', 'test get_message fail')
        self.assertEqual(response1, "fail", 'test get_message fail')