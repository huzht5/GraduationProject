# Generated by Django 3.0 on 2019-01-28 08:39

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('myapp', '0002_auto_20190123_1947'),
    ]

    operations = [
        migrations.CreateModel(
            name='AccountBlog',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('accountid', models.CharField(max_length=40)),
                ('message', models.CharField(max_length=40)),
            ],
        ),
        migrations.CreateModel(
            name='AccountFollow',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('accountid', models.CharField(max_length=40)),
                ('followerid', models.CharField(max_length=40)),
            ],
        ),
        migrations.CreateModel(
            name='Club',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=40)),
                ('ownerid', models.CharField(max_length=40)),
                ('information', models.CharField(max_length=40)),
                ('count', models.IntegerField(default=0)),
            ],
        ),
        migrations.CreateModel(
            name='ClubBlog',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=40)),
                ('message', models.CharField(max_length=40)),
            ],
        ),
        migrations.CreateModel(
            name='ClubFollow',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=40)),
                ('followerid', models.CharField(max_length=40)),
            ],
        ),
        migrations.AddField(
            model_name='account',
            name='count',
            field=models.IntegerField(default=0),
        ),
        migrations.AddField(
            model_name='account',
            name='information',
            field=models.CharField(default=0, max_length=40),
            preserve_default=False,
        ),
    ]
