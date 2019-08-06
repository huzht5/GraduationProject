from django.test import TestCase
from myapp import models
from myapp import update

class UpdateUnitTestCase(TestCase):
    def setUp(self):
        # 初始化时，往Account表中插入一个用户、往Club表中插入一个社团、往ClubSign表中插入一个签到活动
        models.Account.objects.create(accountid="1", password="1", name="1", information="1", count="0")
        models.Club.objects.create(name="动漫社", ownerid="1", ownername="1", information="1", count="0")
        models.ClubSign.objects.create(name="动漫社", time="2019-04-04  20:13:24", message="1", state="可签到")

    def test_change_account_information(self):
        # 测试正常修改个人信息与修改不存在的用户的信息
        response = update.change_account_information("1", "2", "2")
        response1 = update.change_account_information("2", "2", "2")
        self.assertEqual(response, "changeaccountinformation", 'test change_account_information fail')
        self.assertEqual(response1, "fail", 'test change_account_information fail')

    def test_change_club_information(self):
        # 测试正常修改社团简介与修改不存在的社团的简介
        response = update.change_club_information("动漫社", "1")
        response1 = update.change_club_information("篮球社", "1")
        self.assertEqual(response, "changeclubinformation", 'test change_club_information fail')
        self.assertEqual(response1, "fail", 'test change_club_information fail')

    def test_stop_club_sign(self):
        # 测试关注用户、关注不存在的用户与重复关注用户
        response = update.stop_club_sign("动漫社", "2019-04-04  20:13:24")
        response1 = update.stop_club_sign("篮球社", "2019-04-04  20:13:24")
        self.assertEqual(response, "stopclubsign", 'test stop_club_sign fail')
        self.assertEqual(response1, "fail", 'test stop_club_sign fail')