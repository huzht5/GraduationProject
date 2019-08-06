from django.test import TestCase
from myapp import models
from myapp import insert

class InsertUnitTestCase(TestCase):
    def setUp(self):
        # 初始化时，往Account表中插入两个用户、往Club表中插入一个社团、往ClubSign表中插入两个签到活动
        models.Account.objects.create(accountid="1", password="1", name="1", information="1", count="0")
        models.Account.objects.create(accountid="2", password="2", name="2", information="2", count="0")
        models.Club.objects.create(name="动漫社", ownerid="1", ownername="1", information="1", count="0")
        models.ClubSign.objects.create(name="动漫社", time="2019-04-04  20:13:24", message="1", state="可签到")
        models.ClubSign.objects.create(name="动漫社", time="2019-04-04  20:15:24", message="1", state="签到结束")

    def test_register(self):
        # 测试重复注册与正常注册
        response = insert.register("1", "1", "1", "1", "0")
        response1 = insert.register("3", "3", "3", "3", "0")
        self.assertEqual(response, "fail", 'test register fail')
        self.assertEqual(response1, "registered", 'test register fail')

    def test_follow_club(self):
        # 测试关注社团、关注不存在的社团与重复关注社团
        response = insert.follow_club("1", "动漫社")
        response1 = insert.follow_club("1", "篮球社")
        response2 = insert.follow_club("1", "动漫社")
        self.assertEqual(response, "followclub", 'test follow_club fail')
        self.assertEqual(response1, "fail", 'test follow_club fail')
        self.assertEqual(response2, "fail", 'test follow_club fail')

    def test_follow_account(self):
        # 测试关注用户、关注不存在的用户与重复关注用户
        response = insert.follow_account("1", "2")
        response1 = insert.follow_account("1", "3")
        response2 = insert.follow_account("1", "2")
        self.assertEqual(response, "followaccount", 'test follow_account fail')
        self.assertEqual(response1, "fail", 'test follow_account fail')
        self.assertEqual(response2, "fail", 'test follow_account fail')

    def test_create_club(self):
        # 测试重复创建社团与正常创建社团
        response = insert.create_club("1", "1", "动漫社", "1", "0")
        response1 = insert.create_club("1", "1", "篮球社", "1", "0")
        self.assertEqual(response, "fail", 'test create_club fail')
        self.assertEqual(response1, "createclub", 'test create_club fail')

    def test_create_account_blog(self):
        # 测试正常发布个人动态、使用不存在的用户发布与重复发布
        response = insert.create_account_blog("1", "2019-04-04  20:14:24", "1")
        response1 = insert.create_account_blog("3", "2019-04-04  20:14:24", "1")
        response2 = insert.create_account_blog("1", "2019-04-04  20:14:24", "1")
        self.assertEqual(response, "createaccountblog", 'test create_account_blog fail')
        self.assertEqual(response1, "fail", 'test create_account_blog fail')
        self.assertEqual(response2, "fail", 'test create_account_blog fail')

    def test_create_club_blog(self):
        # 测试正常发布社团公告、使用不存在的社团发布与重复发布
        response = insert.create_club_blog("动漫社", "2019-04-04  20:14:24", "1")
        response1 = insert.create_club_blog("篮球社", "2019-04-04  20:14:24", "1")
        response2 = insert.create_club_blog("动漫社", "2019-04-04  20:14:24", "1")
        self.assertEqual(response, "createclubblog", 'test create_club_blog fail')
        self.assertEqual(response1, "fail", 'test create_club_blog fail')
        self.assertEqual(response2, "fail", 'test create_club_blog fail')

    def test_create_club_sign(self):
        # 测试正常发布签到活动、使用不存在的社团发布与重复发布
        response = insert.create_club_sign("动漫社", "2019-04-04  20:14:24", "1", "可签到")
        response1 = insert.create_club_sign("篮球社", "2019-04-04  20:14:24", "1", "可签到")
        response2 = insert.create_club_sign("动漫社", "2019-04-04  20:14:24", "1", "可签到")
        self.assertEqual(response, "createclubsign", 'test create_club_sign fail')
        self.assertEqual(response1, "fail", 'test create_club_sign fail')
        self.assertEqual(response2, "fail", 'test create_club_sign fail')

    def test_sign(self):
        # 测试正常签到、签到不存在的活动、重复签到与签到已结束的活动
        response = insert.sign("1", "动漫社", "2019-04-04  20:13:24", "2019-04-04  20:15:24")
        response1 = insert.sign("1", "篮球社", "2019-04-04  20:13:24", "2019-04-04  20:15:24")
        response2 = insert.sign("1", "动漫社", "2019-04-04  20:13:24", "2019-04-04  20:15:24")
        response3 = insert.sign("1", "动漫社", "2019-04-04  20:15:24", "2019-04-04  20:15:24")
        self.assertEqual(response, "sign", 'test sign fail')
        self.assertEqual(response1, "fail", 'test sign fail')
        self.assertEqual(response2, "signed", 'test sign fail')
        self.assertEqual(response3, "fail", 'test sign fail')

    def test_send_message(self):
        # 测试正常发送信息、发送信息给不存在的用户与不存在的用户发送信息
        response = insert.send_message("1", "2", "2019-04-04  20:13:24", "1")
        response1 = insert.send_message("1", "3", "2019-04-04  20:13:24", "1")
        response2 = insert.send_message("3", "2", "2019-04-04  20:13:24", "1")
        self.assertEqual(response, "sendmessage", 'test send_message fail')
        self.assertEqual(response1, "fail", 'test send_message fail')
        self.assertEqual(response2, "fail", 'test send_message fail')