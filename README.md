
## AgensLib_ServiceGenerator
---

This Beta platform has the purpose of help developers to code faster mobile applications. This platform uses a Postman collection (JSON definition of web services)[https://www.postman.com/collection/]. 

The platform will take as input a the Postman JSON file of web services, to perform parallel micro-tasks of code generation, those tasks will be collaboratively performed by Agents. Those agents will handle java coding tasks accordingly to architectural and design patterns, such as Singleton, Data Access Object (DAO), and Intetactor Callbacks. 

Those classes will be structured as: 

	AgensLib
	├── entities
	├── network 
	│   └─── backend
	│	 └──── ServiceBackend.java
	│   └─── error
	│	 └──── Error.java
	├─────── services
	│	 ├──── Api.java
	│	 ├──── ServiceConfig.java
	│	 ├──── ServiceRequest.java
	│	 ├──── WebService1
	│	 │     ├──── WebService1Dao.java
	│	 │     ├──── WebService1DaoImpl.java
	│	 │     ├──── WebService1Interactor.java
	│	 │     ├──── WebService1Request.java
	│	 │     └──── WebService1Response.java
	│	 ├──── WebService2
	│	 │     └──── ...
	│	 ├──── WebServiceN 
		       └──── ... 

---

# Requirements 
	
 If you want to run the GUI application you have the Mac bundle application (.app). In addition, you can run the source code provided, with the following system requirements already installed.  

	- Apache Maven 3.6.2 
	- Java version 11.0.5+
	
You can test your system if you have correctly maven installed with java typing: 
``` shell 
$ mvn --version
$ Apache Maven 3.6.2 (40f52333136460af0dc0d7232c0dc0bcf0d9e117;)
$ Maven home: /usr/local/Cellar/maven/3.6.2/libexec
$ Java version: 11.0.5, vendor: Oracle Corporation, runtime: /Library/Java/JavaVirtualMachines/jdk-11.0.5.jdk/Contents/Home
```

---

# How to use 

Open a terminal and download the repository by using the git command:

``` shell 
$ git clone https://vanalatorre@bitbucket.org/vanalatorre/agenslib_servicegenerator.git
```
Then locate to the root folder 
``` shell
$ cd agenslib_servicegenerator 
```
You will see the next folders and files: 

 	- AgensLib: It will contain a Android app basic template. 
	- AgensLib.app: The executable Beta platform to generate the java classes. 
	- AgensLibApp: The source code of the Mac bundle.
	
Once you execute the MAS platform you should see the following GUI: 

[AgensLib Screenshot](AgensLib_ScreenShot.png)

In the left side you will: 

1. Write an error message that will be taken as a generic error when the server-side response with an error. (You can change these errors manually in the DaoImpl.java file of each web service.)
2. Upload the postman collection file to be analyzed by the MAS platform. 
3. Browse an output folder where all the classes will be stored. 

On the right side, will appear:
	> The [JADE platform](https://jade.tilab.com) which is a software that provides support for Multi-Agent
	> Systems. You can ignore this window, (Don't close it).

Once all the field are filled, you can see the "Generate classes" button to start processing. 

Once the program finished. You can manually copy those files to your project or use the template provided and generate an AAR library. 

> You should modify the android template as your needs. Before generating the AAR files. 

Using the template provided, type the following commands for a Debug AAR: 
``` shell
$ cd AgensLib
$ gradle assembleDebug 
```
or to export a Release type:
``` shell
$ cd AgensLib
$ gradle assembleRelease
```

The AAR will be generated in the folder at: 
	- agenslib/build/outputs/aar/agenslib-debug.aar 
	- agenslib/build/outputs/aar/agenslib-release.aar 

---

# License

The content of this project itself is licensed under the [GNU General Public License](https://www.gnu.org/licenses/licenses.html#GPL)

--- 



