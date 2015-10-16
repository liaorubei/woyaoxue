create table document(Id int primary key,LevelId int,FolderId int,TitleOne varchar,TitleTwo varchar,SoundPath varchar,DownloadPath varchar,Length long,Duration int,Md5 varchar,IsDownload int ,ModifyTime varchar);
create table    level(Id int primary key,Name varchar);
create table   folder(Id int primary key,Name varchar);
create table UrlCache(Url varchar primary key,Json text,UpdateAt long);--请求缓存地址