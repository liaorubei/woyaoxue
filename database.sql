create table document(Id int primary key,LevelId int,FolderId int,Title varchar,SoundPath varchar,IsDownload int,Json varchar);
create table    level(Id int primary key,Name varchar);
create table   folder(Id int primary key,Name varchar);
create table UrlCache(Url varchar primary key,Json text,UpdateAt long);--请求缓存地址