USE [master]
GO
/****** Object:  Database [EmailDatabase]    Script Date: 03/22/2010 16:02:45 ******/
IF  EXISTS (SELECT name FROM sys.databases WHERE name = N'EmailDatabase')
DROP DATABASE [EmailDatabase]
GO
USE [master]
GO
/****** Object:  Database [EmailDatabase]    Script Date: 03/22/2010 16:03:08 ******/
CREATE DATABASE [EmailDatabase] ON  PRIMARY 
( NAME = N'EmailDatabase', FILENAME = N'c:\Program Files (x86)\Microsoft SQL Server\MSSQL.1\MSSQL\DATA\EmailDatabase.mdf' , SIZE = 3072KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'EmailDatabase_log', FILENAME = N'c:\Program Files (x86)\Microsoft SQL Server\MSSQL.1\MSSQL\DATA\EmailDatabase_log.ldf' , SIZE = 1024KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
EXEC dbo.sp_dbcmptlevel @dbname=N'EmailDatabase', @new_cmptlevel=90
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [EmailDatabase].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [EmailDatabase] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [EmailDatabase] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [EmailDatabase] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [EmailDatabase] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [EmailDatabase] SET ARITHABORT OFF 
GO
ALTER DATABASE [EmailDatabase] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [EmailDatabase] SET AUTO_CREATE_STATISTICS ON 
GO
ALTER DATABASE [EmailDatabase] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [EmailDatabase] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [EmailDatabase] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [EmailDatabase] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [EmailDatabase] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [EmailDatabase] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [EmailDatabase] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [EmailDatabase] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [EmailDatabase] SET  ENABLE_BROKER 
GO
ALTER DATABASE [EmailDatabase] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [EmailDatabase] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [EmailDatabase] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [EmailDatabase] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [EmailDatabase] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [EmailDatabase] SET  READ_WRITE 
GO
ALTER DATABASE [EmailDatabase] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [EmailDatabase] SET  MULTI_USER 
GO
ALTER DATABASE [EmailDatabase] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [EmailDatabase] SET DB_CHAINING OFF 


USE [EmailDatabase]
GO
/****** Object:  Table [dbo].[tblEmail]    Script Date: 03/22/2010 16:02:17 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tblEmail]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[tblEmail](
	[EmailId] [int] IDENTITY(1,1) NOT NULL,
	[Label] [varchar](50) NOT NULL,
	[FileName] [varchar](100) NOT NULL,
	[ToId] [varchar](50) NOT NULL,
	[FromId] [varchar](50) NOT NULL,
	[LastAccessTime] [datetime] NULL,
 CONSTRAINT [PK_tblEmail] PRIMARY KEY CLUSTERED 
(
	[EmailId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  StoredProcedure [dbo].[p_InsertUpdateEmailTuple]    Script Date: 03/22/2010 16:02:15 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[p_InsertUpdateEmailTuple]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[p_InsertUpdateEmailTuple]
@FileName VARCHAR(100),
@ToId VARCHAR(50),
@FromId VARCHAR(50),
@Label VARCHAR(50),
@IsOk BIT OUTPUT,
@Result VARCHAR(100) OUTPUT
AS
BEGIN
	SET NOCOUNT ON;
	
	DECLARE @IsFilePresent INT
	
	SELECT @IsFilePresent = Count(*) FROM tblEmail e WHERE e.[FileName] = @FileName
	
	IF @IsFilePresent = 0
	BEGIN
		INSERT INTO tblEmail (Label, [FileName], ToId, FromId, LastAccessTime)
			VALUES (@Label, @FileName, @ToId, @FromId, GETDATE())
		SET @IsOk = 1
		SET @Result = ''Value inserted''
	END
	ELSE
	BEGIN
		UPDATE tblEmail SET Label = @Label, LastAccessTime = GETDATE()
			WHERE [FileName] = @FileName
		SET @IsOk = 1
		SET @Result = ''Value updated''
	END
END
' 
END
GO
