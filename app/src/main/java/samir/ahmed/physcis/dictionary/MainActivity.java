package samir.ahmed.physcis.dictionary;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        copyDatabase();
        final Database database = new Database(this);
        final EditText ed = (EditText) findViewById(R.id.ed);

        final ListView listView = (ListView) findViewById(R.id.result);
        ;

        assert ed != null;
        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                ArrayList<String> results = new ArrayList<>();
                ArrayList<String> subResults = new ArrayList<>();

                String term = ed.getText().toString();
                if (term.length() > 0) {
                    Cursor cursor = database.getReadableDatabase().
                            query("words", null, "en_word LIKE '" + term + "%'",
                                    null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            results.add(cursor.getString(1));
                            subResults.add(cursor.getString(2));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }

                assert listView != null;
                listView.setAdapter(new Adapter(results,subResults));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void copyDatabase() {
        final String DATABASE_NAME = "dictionary.db";
        String dir = getApplicationInfo().dataDir + "/databases";
        File file = new File(dir);
        boolean mkdir = false;
        if (!file.exists()) {
            mkdir = file.mkdir();
        }
        if (mkdir || !Arrays.asList(databaseList()).contains(DATABASE_NAME)) {
            try {
                InputStream inputStream = getAssets().open(DATABASE_NAME);
                OutputStream outputStream = new FileOutputStream(dir + "/" + DATABASE_NAME + "");
                byte[] buffer = new byte[1024];
                int lenght = inputStream.read(buffer);
                while (lenght != -1) {
                    outputStream.write(buffer, 0, lenght);
                    lenght = inputStream.read(buffer);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class Adapter extends BaseAdapter {
        private ArrayList<String> title = new ArrayList<>();
        private ArrayList<String> subTitle = new ArrayList<>();

        public Adapter(ArrayList<String> title, ArrayList<String> subTitle) {
            this.title = title;
            this.subTitle = subTitle;
        }

        @Override
        public int getCount() {
            return title.size();
        }

        @Override
        public Object getItem(int position) {
            return title.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(title.get(position));
            viewHolder.subTitle.setText(subTitle.get(position));
            return convertView;
        }

        class ViewHolder {
            TextView title, subTitle;

            public ViewHolder(View itemView) {
                title = (TextView) itemView.findViewById(R.id.title);
                subTitle = (TextView) itemView.findViewById(R.id.subTitle);
            }
        }
    }
}
