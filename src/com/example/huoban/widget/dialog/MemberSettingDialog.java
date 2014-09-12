package com.example.huoban.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.example.huoban.R;

/**
 * InfoDialog.
 */
public class MemberSettingDialog extends Dialog {
	/**
	 * InfoDialog.
	 * 
	 * @param context
	 *            param
	 */
	public MemberSettingDialog(Context context) {
		super(context);
	}

	/**
	 * InfoDialog.
	 * 
	 * @param context
	 *            param
	 * @param theme
	 *            param
	 */
	public MemberSettingDialog(Context context, int theme) {
		super(context, theme);
	}

	/**
	 * Builder.
	 */
	public static class Builder {
		/**
		 * context.
		 */
		private Context context;
		/**
		 * beizhuButtonText.
		 */
		private String beizhuButtonText;
		/**
		 * positiveButtonText.
		 */
		private String positiveButtonText;
		/**
		 * negativeButtonText.
		 */
		private String negativeButtonText;
		/**
		 * negativeButtonText.
		 */
		private String cancelButtonText;

		private TextView message;

		private Button beizhuButton, positiveButton, negativeButton;
		/**
		 * beizhuButtonClickListener.
		 */
		private DialogInterface.OnClickListener beizhuButtonClickListener;
		/**
		 * positiveButtonClickListener.
		 */
		private DialogInterface.OnClickListener positiveButtonClickListener;
		/**
		 * negativeButtonClickListener.
		 */
		private DialogInterface.OnClickListener negativeButtonClickListener;
		/**
		 * cancelButtonClickListener.
		 */
		private DialogInterface.OnClickListener cancelButtonClickListener;

		/**
		 * Context.
		 * 
		 * @param context
		 *            param
		 */
		public Builder(Context context) {
			this.context = context;
		}

		// type 1 加入家庭 2 删除好友
		public void setButton(int type,int familyStatus) {
			if (type == 1) {
				beizhuButton.setVisibility(8);
				positiveButton.setVisibility(0);
				negativeButton.setVisibility(8);
				message.setText(R.string.message_1);
				message.setVisibility(0);
				positiveButton.setTextColor(Color.argb(255, 255, 72, 0));
			} else if (type == 2) {
				beizhuButton.setVisibility(8);
				positiveButton.setVisibility(8);
				negativeButton.setVisibility(0);
				message.setText(R.string.message_2);
				message.setVisibility(0);
			}
		}

		public void cancelBUtton() {
			beizhuButton.setVisibility(0);
			positiveButton.setVisibility(0);
			negativeButton.setVisibility(0);
			message.setVisibility(8);
			positiveButton.setTextColor(Color.argb(255, 74, 74, 74));
		}
		
		public void hiddenButton(){
			positiveButton.setVisibility(8);
		}
		
		public void setBackGroundButton(int bgBacKground,int textColor){
			negativeButton.setBackgroundResource(bgBacKground);
			negativeButton.setTextColor(textColor);
		}

		public void setButtonIsHide(Boolean isHide){
			if(isHide==true){
				beizhuButton.setVisibility(View.GONE);
			}else{
				beizhuButton.setVisibility(View.VISIBLE);
			}
		}
		/**
		 * 是否隐藏第二个button 
		 * */
		public void setTwoButtonIsHide(Boolean isHide){
			if(isHide==true){
				positiveButton.setVisibility(View.GONE);
			}else{
				positiveButton.setVisibility(View.VISIBLE);
			}
		}
		/**
		 * Set the positive button resource and it's listener.
		 * 
		 * @param positiveButtonText
		 *            param
		 * @param listener
		 *            param
		 * @return null
		 */
		public Builder setBeizhuPositiveButton(int beizhuButtonText,
				DialogInterface.OnClickListener listener) {
			this.beizhuButtonText = (String) context.getString(beizhuButtonText);
			this.beizhuButtonClickListener = listener;
			return this; 
		}

		/**
		 * Set the positive button resource and it's listener.
		 * 
		 * @param positiveButtonText
		 *            param
		 * @param listener
		 *            param
		 * @return null
		 */
		public Builder setBeizhuPositiveButton(String beizhuButtonText,
				DialogInterface.OnClickListener listener) {
			this.beizhuButtonText = beizhuButtonText;
			this.beizhuButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener.
		 * 
		 * @param positiveButtonText
		 *            param
		 * @param listener
		 *            param
		 * @return null
		 */
		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText); 
			this.positiveButtonClickListener = listener;
			return this; 
		}

		/**
		 * Set the positive button resource and it's listener.
		 * 
		 * @param positiveButtonText
		 *            param
		 * @param listener
		 *            param
		 * @return null
		 */
		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener.
		 * 
		 * @param negativeButtonText
		 *            param
		 * @param listener
		 *            param
		 * @return null
		 */
		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getString(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener.
		 * 
		 * @param negativeButtonText
		 *            param
		 * @param listener
		 *            param
		 * @return null
		 */
		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener.
		 * 
		 * @param negativeButtonText
		 *            param
		 * @param listener
		 *            param
		 * @return null
		 */
		public Builder setCancelButton(int cancelButtonText,
				DialogInterface.OnClickListener listener) {
			this.cancelButtonText = (String) context.getText(cancelButtonText);
			this.cancelButtonClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener.
		 * 
		 * @param negativeButtonText
		 *            param
		 * @param listener
		 *            param
		 * @return null
		 */
		public Builder setCancelButton(String cancelButtonText,
				DialogInterface.OnClickListener listener) {
			this.cancelButtonText = cancelButtonText;
			this.cancelButtonClickListener = listener;
			return this;
		}

		/**
		 * create dialog.
		 * 
		 * @return null
		 */
		public MemberSettingDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final MemberSettingDialog dialog = new MemberSettingDialog(context,
					R.style.Dialog);
			View layout = inflater.inflate( 
					R.layout.member_setting_dialog_layout_b, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			// set the confirm button
			message = (TextView) layout.findViewById(R.id.message);

			positiveButton = (Button) layout.findViewById(R.id.contact_info_positiveButton);
			positiveButton.setText(positiveButtonText);
			positiveButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					positiveButtonClickListener.onClick(dialog,
							DialogInterface.BUTTON_POSITIVE);
				}
			});

			beizhuButton = (Button) layout.findViewById(R.id.contact_info_remarkname);
			beizhuButton.setText(beizhuButtonText);
			beizhuButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					beizhuButtonClickListener.onClick(dialog,
							DialogInterface.BUTTON_POSITIVE);
				}
			});
			// set the cancel button
			negativeButton = (Button) layout.findViewById(R.id.contact_info_negativeButton);
			negativeButton.setText(negativeButtonText);
			negativeButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					negativeButtonClickListener.onClick(dialog,
							DialogInterface.BUTTON_NEGATIVE);
				}
			}); 

			// set the content message
			if (cancelButtonText != null) {
				((Button) layout.findViewById(R.id.contact_info_cancelButton))
						.setText(cancelButtonText);
				if (cancelButtonClickListener != null) {
					((Button) layout.findViewById(R.id.contact_info_cancelButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									cancelButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.contact_info_cancelButton).setVisibility(View.GONE);
			}
			dialog.setContentView(layout);
			return dialog;
		}

		/**
		 * items click listener.
		 * 
		 * @param arrayFruit
		 *            param
		 * @param onClickListener
		 *            param
		 */
		public void setItems(String[] arrayFruit,
				OnClickListener onClickListener) {

		}

	}
}
