from django.test import Client,TestCase
from myapp import models

class UpdateInterfaceTestCase(TestCase):
    def setUp(self):
        super(UpdateInterfaceTestCase, self).setUp()
        self.client = Client(enforce_csrf_checks=True)
        # 初始化时，往Account表中插入一个用户、往Club表中插入一个社团、往ClubSign表中插入一个签到活动
        models.Account.objects.create(accountid="1", password="1", name="1", information="1", count="0")
        models.Club.objects.create(name="动漫社", ownerid="1", ownername="1", information="1", count="0")
        models.ClubSign.objects.create(name="动漫社", time="2019-04-04  20:13:24", message="1", state="可签到")

    def test_change_account_information(self):
        # 测试正常修改个人信息与修改不存在的用户的信息
        response = self.client.post('/update/change_account_information/', {'accountid':'1','name':'2','information':'2'})
        response1 = self.client.post('/update/change_account_information/', {'accountid':'2','name':'2','information':'2'})
        self.assertEqual(response.content, b"changeaccountinformation", 'test change_account_information fail')
        self.assertEqual(response1.content, b"fail", 'test change_account_information fail')

    def test_change_club_information(self):
        # 测试正常修改社团简介与修改不存在的社团的简介
        response = self.client.post('/update/change_club_information/', {'name':'动漫社','information':'1'})
        response1 = self.client.post('/update/change_club_information/', {'name':'篮球社','information':'1'})
        self.assertEqual(response.content, b"changeclubinformation", 'test change_club_information fail')
        self.assertEqual(response1.content, b"fail", 'test change_club_information fail')

    def test_stop_club_sign(self):
        # 测试关注用户、关注不存在的用户与重复关注用户
        response = self.client.post('/update/stop_club_sign/', {'name':'动漫社','time':'2019-04-04  20:13:24'})
        response1 = self.client.post('/update/stop_club_sign/', {'name':'篮球社','time':'2019-04-04  20:13:24'})
        self.assertEqual(response.content, b"stopclubsign", 'test stop_club_sign fail')
        self.assertEqual(response1.content, b"fail", 'test stop_club_sign fail')