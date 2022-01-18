# Crop Image

[![](https://jitpack.io/v/tk.nkduy/CropImage.svg)](https://jitpack.io/#tk.nkduy/CropImage)

The library is a highly configurable widget for image cropping.

## Gradle 
Add this into your dependencies block.
```
compile 'tk.nkduy:cropimage:1.0'
```
## Sample
Please see the [sample app](sample/src/main/java/tk/nkduy/cropimage/sample) for library usage examples.

### Usage:
 Add CropImage to your xml:
```xml
<tk.nkduy.cropimage.CropImageView
  android:id="@+id/crop_view"
  android:layout_width="match_parent"
  android:layout_height="match_parent" />
```
### Image saving
```java
cropView.crop(new CropImageSaveConfig.Builder(destinationUri)
  .setCompressFormat(Bitmap.CompressFormat.PNG)
  .setSize(outWidth, outHeight) //Optional. If not specified, SRC dimensions will be used
  .setQuality(100) //Hint for lossy compression formats
  .build());
```
### Callbacks
Cropped region saved callback. When crop request completes, a broadcast is sent. You can either listen to it using the CropImageView intance
```java
cropView.setCropSaveCompleteListener(bitmapUri -> {
  //Do something
});

cropView.setErrorListener(error -> {
  //Do something
});
```
or work directly with a broadcast receiver. The advantage is that it can be used from any part of the app, where you have an access to `Context`.
```java
CropImageResultReceiver resultReceiver = new CropImageResultReceiver();
resultReceiver.setListener(resultListener);
resultReceiver.register(context);

//Don't forget to unregister it when you are done
resultReceiver.unregister(context);
```
You can subscribe for changes in `CropImageView`s configs. Listeners will be notified anytime `.apply()` is called.
```java
cropImageView.configureOverlay().addConfigChangeListener(listener);
cropImageView.configureImage().addConfigChangeListener(listener)
```
### Basic View Configuration
* Enable user to resize a crop area. Default is true. 
```java
app:ci_dynamic_aspect_ratio="true|false"

cropView.configureOverlay()
  .setDynamicCrop(enabled)
  .apply();
```
* Draw a 3x3 grid. Default is true.
```java
app:ci_draw_grid="true|false"

cropView.configureOverlay()
  .setShouldDrawGrid(draw)
  .apply();
```
* Set an initial crop area's aspect ratio.
```java
app:ci_aspect_ratio_w="16"
app:ci_aspect_ratio_h="9"

cropView.configureOverlay()
  .setAspectRatio(new AspectRatio(16, 9))
  .setAspectRatio(AspectRatio.IMG_SRC) //If you want crop area to be equal to the dimensions of an image
  .apply();
```
* Initial image position. Behavior is similar to ImageView's scaleType.
```java
app:ci_initial_position="centerCrop|centerInside"

cropView.configureImage()
  .setImageInitialPosition(position)
  .apply();
```
* Set current scale of the image.
```java
//Value is a float from 0.01f to 1
cropImageView.configureImage()
  .setScale(scale)
  .apply();
```
* Enable pinch gesture to scale an image.
```java
app:ci_scale_enabled="true|false"

cropView.configureImage()
  .setImageScaleEnabled(enabled)
  .apply();
```
* Enable finger drag to translate an image.
```java
app:ci_translation_enabled="true|false"

cropView.configureImage()
  .setImageTranslationEnabled(enabled)
  .apply();
```
* Choosing from default crop area shapes. Default is rectangle.
```java
app:ci_crop_shape="rectangle|oval"

cropView.configureOverlay()
  .setCropShape(new CropImageRectShape(cropView.configureOverlay()))
  .setCropShape(new CropImageOvalShape(cropView.configureOverlay()))
  .apply();
```
* You can set a min-max scale. Default min is 0.7, default max is 3.
```java
app:ci_max_scale="1f"

cropView.configureImage()
  .setMinScale(minScale)
  .setMaxScale(maxScale)
  .apply();
```
* Crop area min size.
```java
app:ci_min_crop_width="40dp"
app:ci_min_crop_height="40dp"

cropView.configureOverlay()
  .setMinWidth(dps)
  .setMinHeight(dps)
  .apply();
```
* Dimensions.
```java
app:ci_border_width="1dp"
app:ci_corner_width="1dp"
app:ci_grid_width="1dp"

cropView.configureOverlay()
  .setBorderStrokeWidth(dps)
  .setCornerStrokeWidth(dps)
  .setGridStrokeWidth(dps)
  .apply();
```
* Colors.
```java
app:ci_border_color="#fff"
app:ci_corner_color="#fff"
app:ci_grid_color="#fff"
app:ci_overlay_color="#fff"

cropView.configureOverlay()
  .setBorderColor(Color.WHITE)
  .setCornerColor(Color.WHITE)
  .setGridColor(Color.WHITE)
  .setOverlayColor(Color.WHITE)
  .apply();
```
### Advanced View Configuration
You can work directly with `Paint` objects. This gives you an ability, for example, to draw a grid with dashed effect.
```java
Paint gridPaint = cropView.configureOverlay()
  .getCropShape()
  .getGridPaint();
gridPaint.setPathEffect(new DashPathEffect(new float[] {interval, interval}, 0));
```
You can obtain other `Paint`s in the same way.
```java
CropImageOverlayConfig config = cropView.configureOverlay();
CropImageShape shape = config.getCropShape();
shape.getGridPaint();
shape.getBorderPaint();
shape.getCornerPaint();
```
You can also create custom crop area shapes. Just extend `CropImageShape` (for an example refer to [CropImageOvalShape](library/src/main/java/tk/nkduy/cropimage/shape/CropImageOvalShape.java)) and set an instance of you class using:
```java
cropView.configureOverlay()
  .setCropShape(new MyAwesomeShape())
  .apply();
```

## License
```
Copyright © 2021 NKDuy.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
