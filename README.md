# ImageGroupView

This view main solve the view data restore. The activity main killed when open camera. So some data of the group view will not restore by themself. This view save the state and then restore to recreate the layout.

Screeshot
====
![](/screenshot/screenshot.gif)

Show Images
====
First, add the layout
```xml
    <com.loopeer.android.librarys.imagegroupview.ImageGroupView
            android:id="@+id/images_group"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            apps:childMargin="4dp"
            apps:column="3"
            apps:showAddButton="false" />
```
* childMargin   
      The margin between child, as the left and top.
* column
        You can set as you need  
* showAddButton                 
        When set true, you can add image by album or camera

Then, you can add data.
```java
        ArrayList<String> testdata = createTestData());
        imageGroup.setPhotos(testdata);
```
You can add ClickListener
```java
        imageGroup.setOnImageClickListener(this);
        ......
```
Get data by the method 
```java
        onImageClick(SquareImage clickImage, ArrayList<SquareImage> squareImages, ArrayList<String> allImageInternetUrl)
```
Add Images
====
If you want to add image, you must add this
```java
        imageGroup.setFragmentManager(getSupportFragmentManager());
```
You must get the data from the intent in the result
```java
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageGroupAddAble.onParentResult(requestCode, data);
        }
    }
```

License
====
<pre>
Copyright 2015 Loopeer

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
