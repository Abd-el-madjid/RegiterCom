package com.example.registrecom.Activites;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.registrecom.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class RetrievePDFActivvity extends AppCompatActivity {

    RecyclerView pdfRecyclerView;
    private DatabaseReference pRef;
    Query query;
    ProgressBar progressBar;
private HashMap<String, String> receivedMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pdf);

// Inside RetrievePDFActivvity class
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            // Extract values from the Bundle

            for (String key : bundle.keySet()) {
                String value = bundle.getString(key);
                receivedMap.put(key, value);
            }

            // Now you have the HashMap in receivedMap
            // You can use receivedMap in your activity
        }


    }


 /*   private void showPdf() {
        FirebaseRecyclerOptions<FileinModel> options = new FirebaseRecyclerOptions.Builder<FileinModel>()
                .setQuery(query, FileinModel.class)
                .build();
        FirebaseRecyclerAdapter<FileinModel, Adapter> adapter = new FirebaseRecyclerAdapter<FileinModel, Adapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull  Adapter holder, int position, @NonNull FileinModel model) {

                progressBar.setVisibility(View.GONE);
                holder.pdfTitle.setText(model.getFilename());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setType("application/pdf");
                        intent.setData(Uri.parse(model.getFileurl()));
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public Adapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_item,parent,false);
                Adapter holder = new Adapter(view);
                return holder;
            }
        };

        pdfRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // thats it
    //this is the way you can create a simple pdf reader app
}