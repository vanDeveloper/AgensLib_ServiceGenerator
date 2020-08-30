
## AgensLib_ServiceGenerator
---

This Beta platform has the purpose of help developers to code faster mobile applications. This platform uses a Postman collection (JSON definition of web services)[https://www.postman.com/collection/]. 

The platform will take as input a JSON file of web services, to perform parallel micro-tasks of code generation, those tasks will be collaboratively performed by Agents. 

> Agents consist of autonomous entities, they collaboratively solve tasks,
> they offer more flexibility due to their inherent ability to learn and 
> make autonomous decisions. Agents use their interactions with neighboring
> agents or with the environment to learn new contexts and actions. 
> Subsequently, agents use their knowledge to decide and perform an action 
> on the environment to solve their allocated task.

Those agents will handle java coding tasks accordingly to architectural and design patterns, such as Singleton, Data Access Object (DAO), and Intetactor Callbacks. 

Those classes will be structured as: 

	- entities
	- network 
		- backend
			- ServiceBackend.java
		- error
			- Error.java
		- services
			- Api.java
			- ServiceConfig.java
			- ServiceRequest.java
			- WebService1
				- WebService1Dao.java
				- WebService1DaoImpl.java
				- WebService1Interactor.java
				- WebService1Request.java
				- WebService1Response.java
			- WebService2
				- ... 
			- WebServiceN 
			 	- ...

---

# Requirements 
	
	- Apache Maven 3.6.2 
	- Java version 11.0.5+
	
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
You will see 4 folders: 

	- AgensLib: It will contain a Android template. 
	- Mac: It will contain the executable Beta platform to generate the java classes. 
	- Linux: .....
	- Windows: .....

Once you execute the MAS platform you should see the following GUI: 

[AgensLib Screenshot](AgensLib_ScreenShot.png)

In the left side you will: 

1. Write an error message that will be taken as a generic error when the server-side response with an error. (You can change these errors manually in the DaoImpl.java file of each web service.)
2. Upload the postman collection file to be analyzed by the MAS platform. 
3. Browse an output folder where all the classes will be stored. 

On the right side, will appear the [JADE platform](https://jade.tilab.com) which is a software that provides support for Multi-Agent Systems. You can ignore this window, (Don't close it).

Once the program finished. You can manually copy those files to your project or use the template provided and generate an AAR library. 

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



