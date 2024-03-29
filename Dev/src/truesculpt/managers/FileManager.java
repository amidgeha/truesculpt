package truesculpt.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

//open and save a file, autosave?
public class FileManager extends BaseManager
{
	public class FileElem
	{
		public String name;
		public String objfilename;
		public String imagefilename;
		public Bitmap bmp = null;
	};

	public static String GetRootDirectory()
	{
		return Environment.getExternalStorageDirectory() + "/Truesculpt/Sculptures/";
	}

	public String GetObjectFileName()
	{
		return GetBaseFileName() + "Mesh.obj";
	}

	public String GetImageFileName()
	{
		return GetBaseFileName() + "Image.png";
	}

	public static boolean CheckSculptureExist(String name)
	{
		boolean bRes = false;

		File file = new File(GetRootDirectory() + name);
		bRes = file.exists();

		return bRes;
	}

	public String GetBaseFileName()
	{
		String name = getManagers().getMeshManager().getName();
		String strBasePath = GetRootDirectory() + name + "/";

		// have the object build the directory structure, if needed.
		File basePath = new File(strBasePath);
		basePath.mkdirs();

		return strBasePath;
	}

	public String CreateSnapshotFileName()
	{
		String strBasePath = Environment.getExternalStorageDirectory() + "/Truesculpt/Screenshots/";

		// have the object build the directory structure, if needed.
		File basePath = new File(strBasePath);
		basePath.mkdirs();

		Date date = new Date();
		String name = getManagers().getMeshManager().getName();
		String strFileName = strBasePath + "Img_" + name + "_" + date.toGMTString() + ".png";
		strFileName = strFileName.replaceAll(":", "_");
		strFileName = strFileName.replaceAll(" ", "_");
		return strFileName;
	}

	public static String GetDefaultFileName()
	{
		Date date = new Date();
		String strFileName = "Sculpt_" + date.toGMTString();
		strFileName = strFileName.replaceAll(":", "_");
		strFileName = strFileName.replaceAll(" ", "_");
		return strFileName;
	}

	public ArrayList<FileElem> getFileList()
	{
		getManagers().getUtilsManager();
		String strRootDir = GetRootDirectory();

		ArrayList<FileElem> fileList = new ArrayList<FileElem>();
		File rootDir = new File(strRootDir);
		if (rootDir.exists())
		{
			File[] listFiles = rootDir.listFiles();
			for (File file : listFiles)
			{
				boolean bIsDir = file.isDirectory();
				if (bIsDir)
				{
					FileElem elem = GetFileElemFromFile(file);
					if (elem != null)
					{
						fileList.add(elem);
					}
				}
			}
		}
		return fileList;
	}

	public FileElem GetFileElemFromFile(File file)
	{
		FileElem elem = null;
		String strName = file.getName();
		String strObjName = file.getAbsolutePath() + "/" + "Mesh.obj";
		String strImageName = file.getAbsolutePath() + "/" + "Image.png";
		File ObjName = new File(strObjName);
		File ImageName = new File(strImageName);
		if (ObjName.exists() && ImageName.exists())
		{
			elem = new FileElem();
			elem.objfilename = strObjName;
			elem.name = strName;
			elem.imagefilename = strImageName;
		}
		return elem;
	}

	public FileManager(Context baseContext)
	{
		super(baseContext);
	}

	@Override
	public void onCreate()
	{

	}

	@Override
	public void onDestroy()
	{

	}

	public void deleteFile(FileElem selectedElem)
	{
		if (CheckSculptureExist(selectedElem.name))
		{
			String dir = GetRootDirectory() + selectedElem.name + "/";
			File fileDir = new File(dir);
			File fileMesh = new File(dir + "Mesh.obj");
			File fileImage = new File(dir + "Image.png");
			fileMesh.delete();
			fileImage.delete();
			fileDir.delete();// dir must be emptied
		}
	}

	public FileElem renameFile(FileElem selectedElem, String newName)
	{
		FileElem elem = null;
		if (CheckSculptureExist(selectedElem.name) && !CheckSculptureExist(newName))
		{
			File oldDir = new File(GetRootDirectory() + selectedElem.name);
			File newDir = new File(GetRootDirectory() + newName);
			oldDir.renameTo(newDir);
			elem = GetFileElemFromFile(newDir);
		}
		return elem;
	}
}
