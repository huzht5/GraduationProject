from django.test import TestCase
from myapp import models
from myapp import delete

class DeleteUnitTestCase(TestCase):
    def setUp(self):
        # 初始化时，往Account表中插入两个用户、往Club表中插入一个社团
        # 往ClubFollow表中插入一个关注信息\往AccountFollow表中插入一个关注信息
        # 往AccountBlog表中插入一个用户动态、往ClubBlog表中插入一个社团公告、往ClubSign表中插入一个社团签到活动
        models.Account.objects.create(accountid="1", password="1", name="1", information="1", count="0")
        models.Account.objects.create(accountid="2", password="2", name="2", information="2", count="1")
        models.Club.objects.create(name="动漫社", ownerid="1", ownername="1", information="1", count="1")
        models.ClubFollow.objects.create(name="动漫社", followerid="1")
        models.AccountFollow.objects.create(accountid="2", followerid="1")
        models.AccountBlog.objects.create(accountid="1", time="2019-04-04  20:13:24", message="1")
        models.ClubBlog.objects.create(name="动漫社", time="2019-04-04  20:13:24", message="1")
        models.ClubSign.objects.create(name="动漫社", time="2019-04-04  20:13:24", message="1", state="可签到")

    def test_cancel_follow_club(self):
        # 测试正常取消关注社团、取消关注不存在的社团与重复取消关注
        response = delete.cancel_follow_club("1", "动漫社")
        response1 = delete.cancel_follow_club("1", "篮球社")
        response2 = delete.cancel_follow_club("1", "动漫社")
        self.assertEqual(response, "cancelfollowclub", 'test cancel_follow_club fail')
        self.assertEqual(response1, "fail", 'test cancel_follow_club fail')
        self.assertEqual(response2, "fail", 'test cancel_follow_club fail')

    def test_cancel_follow_account(self):
        # 测试正常取消关注用户、取消关注不存在的用户与重复取消关注
        response = delete.cancel_follow_account("1", "2")
        response1 = delete.cancel_follow_account("1", "3")
        response2 = delete.cancel_follow_account("1", "2")
        self.assertEqual(response, "cancelfollowaccount", 'test cancel_follow_account fail')
        self.assertEqual(response1, "fail", 'test cancel_follow_account fail')
        self.assertEqual(response2, "fail", 'test cancel_follow_account fail')

    def test_delete_club(self):
        # 测试删除社团与删除不存在的社团
        response = delete.delete_club("动漫社")
        response1 = delete.delete_club("篮球社")
        self.assertEqual(response, "deleteclub", 'test delete_club fail')
        self.assertEqual(response1, "fail", 'test delete_club fail')

    def test_delete_account_blog(self):
        # 测试删除用户动态与删除不存在的用户动态
        response = delete.delete_account_blog("1", "2019-04-04  20:13:24")
        response1 = delete.delete_account_blog("2", "2019-04-04  20:13:24")
        self.assertEqual(response, "deleteaccountblog", 'test delete_account_blog fail')
        self.assertEqual(response1, "fail", 'test delete_account_blog fail')

    def test_delete_club_blog(self):
        # 测试删除社团公告与删除不存在的社团公告
        response = delete.delete_club_blog("动漫社", "2019-04-04  20:13:24")
        response1 = delete.delete_club_blog("篮球社", "2019-04-04  20:13:24")
        self.assertEqual(response, "deleteclubblog", 'test delete_club_blog fail')
        self.assertEqual(response1, "fail", 'test delete_club_blog fail')

    def test_delete_club_sign(self):
        # 测试删除签到活动与删除不存在的签到活动
        response = delete.delete_club_sign("动漫社", "2019-04-04  20:13:24")
        response1 = delete.delete_club_sign("篮球社", "2019-04-04  20:13:24")
        self.assertEqual(response, "deleteclubsign", 'test delete_club_sign fail')
        self.assertEqual(response1, "fail", 'test delete_club_sign fail')