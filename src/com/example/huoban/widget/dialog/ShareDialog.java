package com.example.huoban.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.example.huoban.R;

/**
 * InfoDialog.
 */
public class ShareDialog extends Dialog {
	/**
	 * InfoDialog.
	 * 
	 * @param context
	 *            param
	 */
	public ShareDialog(Context context) {
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
	public ShareDialog(Context context, int theme) {
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
		 * positiveButtonClickListener.
		 */
		private DialogInterface.OnClickListener positiveButtonClickListener;
		/**
		 * negativeButtonClickListener.
		 */
		private DialogInterface.OnClickListener negativeButtonClickListener;

		private DialogInterface.OnClickListener cancelButtonClickListener;

		private DialogInterface.OnClickListener buttonClickListener;

		/**
		 * Context.
		 * 
		 * @param context
		 *            param
		 */
		public Builder(Context context) {
			this.context = context;
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
		public Builder setPositiveButton(
				DialogInterface.OnClickListener listener) {
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
		public Builder setButton(DialogInterface.OnClickListener listener) {
			this.buttonClickListener = listener;
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
		public Builder setNegativeButton(
				DialogInterface.OnClickListener listener) {
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
		public Builder setCancelButton(DialogInterface.OnClickListener listener) {
			this.cancelButtonClickListener = listener;
			return this;
		} 

		/**
		 * create dialog.
		 * 
		 * @return null
		 */
		public ShareDialog create() {  
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final ShareDialog dialog = new ShareDialog(context, R.style.dialog);
			View layout = inflater.inflate(R.layout.share_dialog_layout, null);
			dialog.addContentView(layout, new LayoutParams( 
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			// set the confirm button 
			if (positiveButtonClickListener != null) {
				((TextView) layout.findViewById(R.id.contact_info_positiveButton))
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								positiveButtonClickListener.onClick(dialog,
										DialogInterface.BUTTON_POSITIVE);
							}
						});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.contact_info_positiveButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (negativeButtonClickListener != null) {
				((TextView) layout.findViewById(R.id.contact_info_negativeButton))
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								negativeButtonClickListener.onClick(dialog,
										DialogInterface.BUTTON_NEGATIVE);
							}
						});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.contact_info_negativeButton).setVisibility(
						View.GONE);
			}
			// set the content message
			if (cancelButtonClickListener != null) {
				((TextView) layout.findViewById(R.id.contact_info_cancelButton))
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								cancelButtonClickListener.onClick(dialog,
										DialogInterface.BUTTON_NEGATIVE);
							}
						});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.contact_info_cancelButton).setVisibility(View.GONE);
			}
			// set the content message
			if (buttonClickListener != null) {
				((Button) layout.findViewById(R.id.button))
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								buttonClickListener.onClick(dialog,
										DialogInterface.BUTTON_NEGATIVE);
							}
						});
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.button).setVisibility(View.GONE);
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
