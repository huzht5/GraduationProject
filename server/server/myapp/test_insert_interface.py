from django.test import Client,TestCase
from myapp import models

class InsertInterfaceTestCase(TestCase):
    def setUp(self):
        super(InsertInterfaceTestCase, self).setUp()
        self.client = Client(enforce_csrf_checks=True)
        # 初始化时，往Account表中插入两个用户、往Club表中插入一个社团、往ClubSign表中插入两个签到活动
        models.Account.objects.create(accountid="1", password="1", name="1", information="1", count="0")
        models.Account.objects.create(accountid="2", password="2", name="2", information="2", count="0")
        models.Club.objects.create(name="动漫社", ownerid="1", ownername="1", information="1", count="0")
        models.ClubSign.objects.create(name="动漫社", time="2019-04-04  20:13:24", message="1", state="可签到")
        models.ClubSign.objects.create(name="动漫社", time="2019-04-04  20:15:24", message="1", state="签到结束")

    def test_register(self):
        # 测试重复注册与正常注册
        response = self.client.post('/insert/register/', {'accountid':'1','password':'1','name':'1','information':'1','count':'0'})
        response1 = self.client.post('/insert/register/', {'accountid':'3','password':'3','name':'3','information':'3','count':'0'})
        self.assertEqual(response.content, b"fail", 'test register fail')
        self.assertEqual(response1.content, b"registered", 'test register fail')

    def test_follow_club(self):
        # 测试关注社团、关注不存在的社团与重复关注社团
        response = self.client.post('/insert/follow_club/', {'followerid':'1','name':'动漫社'})
        response1 = self.client.post('/insert/follow_club/', {'followerid':'1','name':'篮球社'})
        response2 = self.client.post('/insert/follow_club/', {'followerid':'1','name':'动漫社'})
        self.assertEqual(response.content, b"followclub", 'test follow_club fail')
        self.assertEqual(response1.content, b"fail", 'test follow_club fail')
        self.assertEqual(response2.content, b"fail", 'test follow_club fail')

    def test_follow_account(self):
        # 测试关注用户、关注不存在的用户与重复关注用户
        response = self.client.post('/insert/follow_account/', {'followerid':'1','accountid':'2'})
        response1 = self.client.post('/insert/follow_account/', {'followerid':'1','accountid':'3'})
        response2 = self.client.post('/insert/follow_account/', {'followerid':'1','accountid':'2'})
        self.assertEqual(response.content, b"followaccount", 'test follow_account fail')
        self.assertEqual(response1.content, b"fail", 'test follow_account fail')
        self.assertEqual(response2.content, b"fail", 'test follow_account fail')

    def test_create_club(self):
        # 测试重复创建社团与正常创建社团
        response = self.client.post('/insert/create_club/', {'ownerid':'1','ownername':'1','name':'动漫社','information':'1','count':'0'})
        response1 = self.client.post('/insert/create_club/', {'ownerid':'1','ownername':'1','name':'篮球社','information':'1','count':'0'})
        self.assertEqual(response.content, b"fail", 'test create_club fail')
        self.assertEqual(response1.content, b"createclub", 'test create_club fail')

    def test_create_account_blog(self):
        # 测试正常发布个人动态、使用不存在的用户发布与重复发布
        response = self.client.post('/insert/create_account_blog/', {'accountid':'1','time':'2019-04-04  20:14:24','message':'1'})
        response1 = self.client.post('/insert/create_account_blog/', {'accountid':'3','time':'2019-04-04  20:14:24','message':'1'})
        response2 = self.client.post('/insert/create_account_blog/', {'accountid':'1','time':'2019-04-04  20:14:24','message':'1'})
        self.assertEqual(response.content, b"createaccountblog", 'test create_account_blog fail')
        self.assertEqual(response1.content, b"fail", 'test create_account_blog fail')
        self.assertEqual(response2.content, b"fail", 'test create_account_blog fail')

    def test_create_club_blog(self):
        # 测试正常发布社团公告、使用不存在的社团发布与重复发布
        response = self.client.post('/insert/create_club_blog/', {'name':'动漫社','time':'2019-04-04  20:14:24','message':'1'})
        response1 = self.client.post('/insert/create_club_blog/', {'name':'篮球社','time':'2019-04-04  20:14:24','message':'1'})
        response2 = self.client.post('/insert/create_club_blog/', {'name':'动漫社','time':'2019-04-04  20:14:24','message':'1'})
        self.assertEqual(response.content, b"createclubblog", 'test create_club_blog fail')
        self.assertEqual(response1.content, b"fail", 'test create_club_blog fail')
        self.assertEqual(response2.content, b"fail", 'test create_club_blog fail')

    def test_create_club_sign(self):
        # 测试正常发布签到活动、使用不存在的社团发布与重复发布
        response = self.client.post('/insert/create_club_sign/', {'name':'动漫社','time':'2019-04-04  20:14:24','message':'1','state':'可签到'})
        response1 = self.client.post('/insert/create_club_sign/', {'name':'篮球社','time':'2019-04-04  20:14:24','message':'1','state':'可签到'})
        response2 = self.client.post('/insert/create_club_sign/', {'name':'动漫社','time':'2019-04-04  20:14:24','message':'1','state':'可签到'})
        self.assertEqual(response.content, b"createclubsign", 'test create_club_sign fail')
        self.assertEqual(response1.content, b"fail", 'test create_club_sign fail')
        self.assertEqual(response2.content, b"fail", 'test create_club_sign fail')

    def test_sign(self):
        # 测试正常签到、签到不存在的活动、重复签到与签到已结束的活动
        response = self.client.post('/insert/sign/', {'accountid':'1','name':'动漫社','time':'2019-04-04  20:13:24','signtime':'2019-04-04  20:15:24'})
        response1 = self.client.post('/insert/sign/', {'accountid':'1','name':'篮球社','time':'2019-04-04  20:13:24','signtime':'2019-04-04  20:15:24'})
        response2 = self.client.post('/insert/sign/', {'accountid':'1','name':'动漫社','time':'2019-04-04  20:13:24','signtime':'2019-04-04  20:15:24'})
        response3 = self.client.post('/insert/sign/', {'accountid':'1','name':'动漫社','time':'2019-04-04  20:15:24','signtime':'2019-04-04  20:15:24'})
        self.assertEqual(response.content, b"sign", 'test sign fail')
        self.assertEqual(response1.content, b"fail", 'test sign fail')
        self.assertEqual(response2.content, b"signed", 'test sign fail')
        self.assertEqual(response3.content, b"fail", 'test sign fail')

    def test_send_message(self):
        # 测试正常发送信息、发送信息给不存在的用户与不存在的用户发送信息
        response = self.client.post('/insert/send_message/', {'senderid':'1','receiverid':'2','time':'2019-04-04  20:13:24','message':'1'})
        response1 = self.client.post('/insert/send_message/', {'senderid':'1','receiverid':'3','time':'2019-04-04  20:13:24','message':'1'})
        response2 = self.client.post('/insert/send_message/', {'senderid':'3','receiverid':'2','time':'2019-04-04  20:13:24','message':'1'})
        self.assertEqual(response.content, b"sendmessage", 'test send_message fail')
        self.assertEqual(response1.content, b"fail", 'test send_message fail')
        self.assertEqual(response2.content, b"fail", 'test send_message fail')