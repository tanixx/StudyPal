package com.example.studypal;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.query.Where;
import com.amplifyframework.datastore.generated.model.Subject;
import com.amplifyframework.datastore.generated.model.SubjectTopic;
import com.amplifyframework.datastore.generated.model.Topic;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link progess_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class progess_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public progess_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment progess_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static progess_fragment newInstance(String param1, String param2) {
        progess_fragment fragment = new progess_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progess_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView subjectRecyclerView = view.findViewById(R.id.subjectRecyclerView);
        TextView emptyTextView = view.findViewById(R.id.emptyTextView);

        List<SubjectWithTopics> subjectWithTopicsList = new ArrayList<>();
        Amplify.Auth.fetchUserAttributes(
                attributes -> Log.i("Auth", "User attributes: " + attributes.toString()),
                error -> Log.e("Auth", "Failed to fetch user attributes", error)
        );


        Amplify.DataStore.query(Subject.class,
                subjects -> {
                    List<Subject> subjectList = new ArrayList<>();
                    while (subjects.hasNext()) {
                        subjectList.add(subjects.next());
                    }

                    // Now load all topics just once
                    Amplify.DataStore.query(Topic.class,
                            allTopics -> {
                                List<Topic> topicList = new ArrayList<>();
                                while (allTopics.hasNext()) {
                                    topicList.add(allTopics.next());
                                }

                                // Group topics by subject
                                for (Subject subject : subjectList) {
                                    List<Topic> related = new ArrayList<>();
                                    for (Topic topic : topicList) {
                                        if (topic.getSubjectId().equals(subject.getId())) {
                                            related.add(topic);
                                        }
                                    }
                                    subjectWithTopicsList.add(new SubjectWithTopics(subject, related));
                                }

                                requireActivity().runOnUiThread(() -> {
                                    if (subjectWithTopicsList.isEmpty()) {
                                        emptyTextView.setVisibility(View.VISIBLE);
                                    } else {
                                        SubjectAdapter adapter = new SubjectAdapter(subjectWithTopicsList);
                                        subjectRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                                        subjectRecyclerView.setAdapter(adapter);
                                    }
                                });
                            },
                            error -> Log.e("Amplify", "Failed to query Topics", error)
                    );
                },
                error -> Log.e("Amplify", "Failed to query Subjects", error)
        );
    }

}