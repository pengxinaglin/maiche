package me.iwf.photopicker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.R;
import me.iwf.photopicker.adapter.PhotoGridAdapter;
import me.iwf.photopicker.adapter.PopupDirectoryListAdapter;
import me.iwf.photopicker.entity.CompressedPhotoEntity;
import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.entity.PhotoDirectory;
import me.iwf.photopicker.event.OnPhotoClickListener;
import me.iwf.photopicker.utils.ImageCaptureManager;
import me.iwf.photopicker.utils.MediaStoreHelper;

import static android.app.Activity.RESULT_OK;
import static me.iwf.photopicker.utils.MediaStoreHelper.INDEX_ALL_PHOTOS;

/**
 * Created by donglua on 15/5/31.
 */
public class PhotoPickerFragment extends Fragment {

	private ImageCaptureManager captureManager;
	private PhotoGridAdapter photoGridAdapter;

	private PopupDirectoryListAdapter listAdapter;
	private List<PhotoDirectory> directories;//展示的图片
	private List<PhotoDirectory> allDirectories;//所有图片包含隐藏图片
	private ArrayList<CompressedPhotoEntity> filterPhotos;//需要隐藏的图片

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		directories = new ArrayList<PhotoDirectory>();
		allDirectories = new ArrayList<>();
		captureManager = new ImageCaptureManager(getActivity());


		MediaStoreHelper.getPhotoDirs(getActivity(),
			new MediaStoreHelper.PhotosResultCallback() {
				@Override
				public void onResultCallback(List<PhotoDirectory> directories) {
					PhotoPickerFragment.this.allDirectories.addAll(directories);//记录所有图片

					//保存筛选过后的图片
					List<PhotoDirectory> newDirectories = new ArrayList<PhotoDirectory>();
					try {
						if (directories != null && getFilterPhotos() != null && !getFilterPhotos().isEmpty()) {
							//需要删掉的图片
							ArrayList<CompressedPhotoEntity> filterPhotos = getFilterPhotos();
							//遍历所有的图片目录
							for (PhotoDirectory directory : directories) {
								//保存此目录下的图片
								List<Photo> photoPaths = new ArrayList<Photo>();
								if (directory.getPhotos() != null && !directory.getPhotos().isEmpty()) {
									//遍历此目录的所有图片
									for (Photo photo : directory.getPhotos()) {
										if (!TextUtils.isEmpty(photo.getPath())) {
											boolean exists = false;
											for (CompressedPhotoEntity photoEntity : filterPhotos) {
												if (photo.getPath().contains(photoEntity.getLocal_photo_name()) || photo.getPath().contains(photoEntity.getSd_photo_name())) {
													exists = true;
													break;
												}
											}
											if (!exists)
												photoPaths.add(photo);
										}
									}
								}
								PhotoDirectory newDirectory = new PhotoDirectory();
								newDirectory.setPhotos(photoPaths);
								newDirectory.setCoverPath(directory.getCoverPath());
								newDirectory.setDateAdded(directory.getDateAdded());
								newDirectory.setId(directory.getId());
								newDirectory.setName(directory.getName());
								//保存此目录
								newDirectories.add(newDirectory);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					photoGridAdapter.notifyDataSetChanged();
					PhotoPickerFragment.this.directories.addAll(newDirectories.isEmpty() ? directories : newDirectories);
					listAdapter.notifyDataSetChanged();
				}
			});
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		setRetainInstance(true);

		final View rootView = inflater.inflate(R.layout.fragment_photo_picker, container, false);

		photoGridAdapter = new PhotoGridAdapter(getActivity(), directories);
		listAdapter = new PopupDirectoryListAdapter(getActivity(), directories);


		RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_photos);
		StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL);
		layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(photoGridAdapter);
		recyclerView.setItemAnimator(new DefaultItemAnimator());

		final Button btSwitchDirectory = (Button) rootView.findViewById(R.id.button);

		final Button showAllPhotos = (Button) rootView.findViewById(R.id.button2);

		final ListPopupWindow listPopupWindow = new ListPopupWindow(getActivity());
		listPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
		listPopupWindow.setAnchorView(btSwitchDirectory);
		listPopupWindow.setAdapter(listAdapter);
		listPopupWindow.setModal(true);
		listPopupWindow.setDropDownGravity(Gravity.BOTTOM);
		listPopupWindow.setAnimationStyle(R.style.Animation_AppCompat_DropDownUp);

		listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				listPopupWindow.dismiss();

				PhotoDirectory directory = directories.get(position);

				btSwitchDirectory.setText(directory.getName());

				photoGridAdapter.setCurrentDirectoryIndex(position);
				photoGridAdapter.notifyDataSetChanged();
			}
		});

		photoGridAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
			@Override
			public void onClick(View v, int position, boolean showCamera) {
				final int index = showCamera ? position - 1 : position;

				List<String> photos = photoGridAdapter.getCurrentPhotoPaths();

				int[] screenLocation = new int[2];
				v.getLocationOnScreen(screenLocation);
				ImagePagerFragment imagePagerFragment =
					ImagePagerFragment.newInstance(photos, index, screenLocation,
						v.getWidth(), v.getHeight());

				((PhotoPickerActivity) getActivity()).addImagePagerFragment(imagePagerFragment);
			}
		});

		photoGridAdapter.setOnCameraClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					Intent intent = captureManager.dispatchTakePictureIntent();
					startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		btSwitchDirectory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (listPopupWindow.isShowing()) {
					listPopupWindow.dismiss();
				} else if (!getActivity().isFinishing()) {
					listPopupWindow.setHeight(Math.round(rootView.getHeight() * 0.8f));
					listPopupWindow.show();
				}

			}
		});

		showAllPhotos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showAllPhotos();
				view.setVisibility(View.GONE);
			}
		});

		return rootView;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
			captureManager.galleryAddPic();
			if (directories.size() > 0) {
				String path = captureManager.getCurrentPhotoPath();
				PhotoDirectory directory = directories.get(INDEX_ALL_PHOTOS);
				directory.getPhotos().add(INDEX_ALL_PHOTOS, new Photo(path.hashCode(), path));
				directory.setCoverPath(path);
				photoGridAdapter.notifyDataSetChanged();
			}
		}
	}


	public PhotoGridAdapter getPhotoGridAdapter() {
		return photoGridAdapter;
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		captureManager.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}


	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		captureManager.onRestoreInstanceState(savedInstanceState);
		super.onViewStateRestored(savedInstanceState);
	}

	public ArrayList<String> getSelectedPhotoPaths() {
		return photoGridAdapter.getSelectedPhotoPaths();
	}

	public ArrayList<CompressedPhotoEntity> getFilterPhotos() {
		return filterPhotos;
	}

	public void setFilterPhotos(ArrayList<CompressedPhotoEntity> filterPhotos) {
		this.filterPhotos = filterPhotos;
	}

	/**
	 * 显示所有的图片
	 */
	public void showAllPhotos() {
		//发送一个广播让app删掉记录
		Intent intent = new Intent("com.haoche51.clearRecordReceiver.action");
		getActivity().sendBroadcast(intent);

		this.directories.clear();
		this.directories.addAll(this.allDirectories);
		this.photoGridAdapter.notifyDataSetChanged();
		this.listAdapter.notifyDataSetChanged();
	}
}
