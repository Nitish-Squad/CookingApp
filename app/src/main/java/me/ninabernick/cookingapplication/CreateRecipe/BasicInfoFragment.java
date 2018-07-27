package me.ninabernick.cookingapplication.CreateRecipe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.ninabernick.cookingapplication.BitmapScaler;
import me.ninabernick.cookingapplication.HomeActivity;
import me.ninabernick.cookingapplication.R;
import me.ninabernick.cookingapplication.models.Recipe;

import static android.app.Activity.RESULT_OK;

public class BasicInfoFragment extends Fragment {

    // layout variables
    private EditText etTitle;
    private EditText etDescription;
    private EditText etHours;
    private EditText etMinutes;
    private ImageView ivRecipeImage;
    private Button btnTake;
    private Button btnChoose;
    private Button btnNext;
    private AutoCompleteTextView acTag1;
    private ImageButton ivAddTag;
    private LinearLayout tags;
    private ArrayList<AutoCompleteTextView> tagList;

    // variables for camera operation
    public final String APP_TAG = "Cooking App";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;
    public String photoFileName = "photo";
    File photoFile;


    private OnFragmentInteractionListener mListener;

    public BasicInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_basic_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // attach all of the layout elements
        etTitle = (EditText) view.findViewById(R.id.etTitle);
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        etHours = (EditText) view.findViewById(R.id.etHours);
        etMinutes = (EditText) view.findViewById(R.id.etMinutes);
        ivRecipeImage = (ImageView) view.findViewById(R.id.ivRecipeImage);
        btnTake = (Button) view.findViewById(R.id.btnTakePhoto);
        btnChoose = (Button) view.findViewById(R.id.btnChoosePhoto);
        btnNext = (Button) view.findViewById(R.id.btnNext);
        // adding tags
        tags = (LinearLayout) view.findViewById(R.id.tags);
        ivAddTag = (ImageButton) view.findViewById(R.id.ivAddTag);



        tagList = new ArrayList<>();

        // adapter for autocompleting tags
        ArrayList<String> tagResources = new ArrayList<>();
        tagResources.addAll(Arrays.asList(getResources().getStringArray(R.array.tags)));
        final ArrayAdapter<String> tagAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, tagResources);


        /*
         * setting up the fragment in case there was back-navigation, notice only the tags are set
         * because they are programtically added and are not preserved but the default elements are
         * retained between back navigation.
         */

        HomeActivity createActivity = (HomeActivity) getActivity();
        Recipe recipe_to_edit = createActivity.recipe_to_add;

        if (recipe_to_edit.getTags() == null){
            AutoCompleteTextView newTag = new AutoCompleteTextView(getContext());
            newTag.setAdapter(tagAdapter);
            newTag.setHint("Add Tag (optional)");
            tags.addView(newTag);
            tagList.add(newTag);
        }
        else{
            List<String> existing_tags = recipe_to_edit.getTags();
            for (int t = 0; t < existing_tags.size(); t++){
                AutoCompleteTextView newTag = new AutoCompleteTextView(getContext());
                newTag.setAdapter(tagAdapter);
                newTag.setText(existing_tags.get(t));
                tags.addView(newTag);
                tagList.add(newTag);
            }
        }


        ivAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AutoCompleteTextView newTag = new AutoCompleteTextView(getContext());
                newTag.setAdapter(tagAdapter);
                newTag.setHint("Add Tag (optional)");
                tags.addView(newTag);
                tagList.add(newTag);
            }
        });


        btnTake.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    onLaunchCamera();
                } catch (IOException e) {
                    Log.d("BasicInfoFragment","Launching camera failed!");
                    e.printStackTrace();
                }
            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onPickPhoto();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO implement logic for switching to the next fragment and updating the overall
                // new recipe method

                HomeActivity createActivity = (HomeActivity) getActivity();

                Recipe new_recipe = createActivity.recipe_to_add;

                new_recipe.setTitle(etTitle.getText().toString());
                new_recipe.setDescription(etDescription.getText().toString());

                String total_time;

                total_time = etHours.getText().toString()+" hours " + etMinutes.getText().toString() + " minutes";

                new_recipe.setTime(total_time);

                new_recipe.setCreatedBy(ParseUser.getCurrentUser().getObjectId());

                new_recipe.setrecipeImage(new ParseFile(photoFile));

                int hours = 0;

                if (etHours.getText().toString() != null){
                    hours = Integer.valueOf(etHours.getText().toString());
                }

                int minutes = 0;

                if (etMinutes.getText().toString() != null){
                    minutes = Integer.valueOf(etMinutes.getText().toString());
                }

                new_recipe.setStandardTime((hours * 60) + minutes);

                // store any tags added

                List<String> tag_holder = new ArrayList<>();


                for (int i = 0; i < tagList.size(); i++) {
                    if (tagList.get(i).getText().toString() != null) {
                        // TODO: overwrite the tags that exist everytime instead of adding
                        tag_holder.add(tagList.get(i).getText().toString());
                    }
                }

                /*
                 * Needs to overwrite the current tags because they shouldn't be lost and otherwise
                 * it would just duplicate tags that already existed + if any tags were edited
                 * their unedited forms would exist.
                 */
                new_recipe.setTags(tag_holder);

                createActivity.recipe_to_add = new_recipe;
                Log.d("tags", new_recipe.getTags().toString());



                // logic for starting the next fragment
                IngredientsFragment fragment2 = new IngredientsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flFragmentContainer, fragment2);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void onLaunchCamera() throws IOException {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName + ".jpg");


        // wrap File object into a content provider
        // required for API >= 24;
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);


        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = rotateBitmapOrientation(photoFile.getPath());
                // RESIZE BITMAP, see section below
                // by this point we have the camera photo on disk
                Bitmap rawTakenImage = rotateBitmapOrientation(photoFile.getPath());
                // See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 1000);

                // Configure byte output stream
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
                File resizedFile = getPhotoFileUri(photoFileName + "_resized.jpg");
                // resizedFile.createNewFile();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(resizedFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // Write the bytes of the bitmap to file
                try {
                    fos.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }



                // Load the taken image into a preview
                ImageView ivPreview = (ImageView) getView().findViewById(R.id.ivRecipeImage);
                ivPreview.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == PICK_PHOTO_CODE){
            if (resultCode == RESULT_OK){
                if (data != null) {
                    Uri photoUri = data.getData();
                    // Do something with the photo based on Uri
                    Bitmap selectedImage = null;
                    try {
                        selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
                    } catch (IOException e) {
                        Log.d("BasicInfoFragment","Could not retrieve image!");
                        e.printStackTrace();
                    }
                    // Load the selected image into a preview
                    ImageView ivPreview = (ImageView) getView().findViewById(R.id.ivRecipeImage);
                    ivPreview.setImageBitmap(selectedImage);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    // Compress the image further
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
                    File resizedFile = getPhotoFileUri(photoFileName + "_resized.jpg");
                    // resizedFile.createNewFile();
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(resizedFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    // Write the bytes of the bitmap to file
                    try {
                        fos.write(bytes.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    photoFile = resizedFile;
                }
            }
            else{
                Toast.makeText(getContext(), "Picture wasn't selected!", Toast.LENGTH_LONG).show();
            }

        }
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }
}
