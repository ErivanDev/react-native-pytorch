# react-native-pytorch

## Getting started

`$ npm install react-native-pytorch --save`

### Mostly automatic installation

`$ react-native link react-native-pytorch`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-pytorch` and add `Pytorch.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libPytorch.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.reactlibrary.PytorchPackage;` to the imports at the top of the file
  - Add `new PytorchPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-pytorch'
  	project(':react-native-pytorch').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-pytorch/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-pytorch')
  	```


## Usage
```javascript
import Pytorch from 'react-native-pytorch';

// TODO: What to do with the module?
Pytorch;
```
