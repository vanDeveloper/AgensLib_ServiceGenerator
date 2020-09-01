
## BETA - AgensLib_ServiceGenerator
---

This platform has the purpose of help developers to code faster android mobile applications. This platform uses a Postman collection [JSON definition of web services](https://www.postman.com/collection/).

The platform will take as input a the Postman JSON file of web services, to perform parallel micro-tasks of code generation, those tasks will be collaboratively performed by Agents. Those agents will handle java coding tasks accordingly to architectural and design patterns, such as Singleton, Data Access Object (DAO), Interactor Callbacks. Under the Retrofit standards.

Those java classes will be structured as:

	AgensLib
	├── entities
	│		└─ POJOs.java
	├── network
	│		├─ backend
	│		│	└─ ServiceBackend.java
	│		├─ error
	│	 	│	└─ Error.java
	│		├─ services
	│	 	│	├─ Api.java
	│	 	│	├─ ServiceConfig.java
	│	 	│	├─ ServiceRequest.java
	│	 	│	├─ WebService1
	│	 	│	│	├─ WebService1Dao.java
	│	 	│	│	├─ WebService1DaoImpl.java
	│	 	│	│	├─ WebService1Interactor.java
	│	 	│	│	├─WebService1Request.java
	│	 	│	│	└─WebService1Response.java
	│	 	│	├─ WebService2
	│	 	│	│	└─ ...
	│	 	│	├─ WebServiceN
	│		│	│	└─ ...

---

# Requirements
	
 If you want to run the GUI application you have the Mac bundle application (.app). In addition, you can run the source code provided, with the following system requirements already installed.

	- Apache Maven 3.6.2
	- Java version 11.0.5+
	
You can test your system if you have correctly maven installed with java typing: 
``` shell 
$ mvn --version   //The result will be displayed below
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

	1. AgensLib:             It will contain an Android app basic template.
	2. AgensLib.app:      The executable platform to generate the java classes.
	3. AgensLibApp:       The source code of the application.
	4. CocktailAPI.json:   A Postman Collection of web services (For testing).

Once you execute the MAS platform you should see the following GUI:

[AgensLib Screenshot](AgensLib_ScreenShot.png)

In the left side you will:

1. Write an error message that will be taken as a generic error when the server-side response with an error. (You can change these errors manually in the DaoImpl.java file of each web service.)
2. Upload the postman collection file to be analyzed by the MAS platform. (You can use the CocktailAPI.json provided).
3. Browse an output folder where all the classes will be stored.

On the right side, will appear:

1. The [JADE platform](https://jade.tilab.com) which is a software that provides support for Multi-Agent Systems. You can ignore this window, (DO NOT CLOSE IT).

Once all the field are filled, you can see the "Generate classes" button to start processing. 

Once the program finished. You can manually copy those files to your project or use the template provided.

> You should modify the android template as your needs. The purpose for that template is just testing the AgensLib results.

Copy the entities folder and the network folder into the root src/main/java/com/navi/agenslib

``` shell
$ cd AgensLib
$ rsync -a YourOutputPath/com/navi/agenslib/ app/src/main/java/com/navi/agenslib
```

Then you can use Android Studio to build the and debug the project.

# Testing CocktailAPI

Once the files are under the com/navi/agenslib. you can add the following java  code to test each web service. By using Android Studio.

> First declaring the Interface Methods for the web service, in this example the
> api has a method called "GetCocktailDetails" then the interface is
> "GetCocktailDetailsInteractor". 

In the MainActivity.java add:

``` java
... implements GetCocktailDetailsInteractor {
```
Then in the method onCreate of that activity add:
``` java
new Thread(new Runnable() {
            @Override
            public void run() {
                new GetCocktailDetailsDaoImpl().getCocktailDetails("11007", MainActivity.this);
            }
        }).start();
```

As you can see, that code is calling to the web service sending the params needed by the web service, [to see more details of CocktailAPI](https://rapidapi.com/theapiguy/api/the-cocktail-db).

Finally, we need to implement the methods of the interface, that are when the method success and when it fails.

``` java
// LISTENERS
    @Override
    public void onGetCocktailDetailsSuccess(GetCocktailDetailsResponse response) {
        Log.println(Log.DEBUG, this.getLocalClassName(), response
        .toString());
    }
    @Override
    public void onGetCocktailDetailsFailure(Error error) {
        Log.println(Log.DEBUG, this.getLocalClassName(), error.getMsg());
    }
```

As a result, you will have the final POJO response of that web service ready to be used in your project.

---

# License

The content of this project itself is licensed under the [GNU General Public License](https://www.gnu.org/licenses/licenses.html#GPL)

---




