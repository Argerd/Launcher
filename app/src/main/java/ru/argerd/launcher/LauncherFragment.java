package ru.argerd.launcher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.argerd.launcher.databinding.FragmentLauncherBinding;
import ru.argerd.launcher.databinding.LabelOfActivityBinding;

public class LauncherFragment extends Fragment {
    public static LauncherFragment newInstance() {
        return new LauncherFragment();
    }

    private List<ResolveInfo> setupListOfActivitiesForAdapter() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> listOfActivities =
                packageManager.queryIntentActivities(startupIntent, 0);
        Collections.sort(listOfActivities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                PackageManager packageManager1 = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        o1.loadLabel(packageManager).toString(),
                        o2.loadLabel(packageManager).toString());
            }
        });
        return listOfActivities;
    }

    private class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LabelOfActivityBinding binding;
        private ResolveInfo resolveInfo;

        public Holder(LabelOfActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.labelActivity.setClickable(true);
            this.binding.labelActivity.setOnClickListener(this);
        }

        public void bind(ResolveInfo resolveInfo) {
            this.resolveInfo = resolveInfo;
            PackageManager packageManager = getActivity().getPackageManager();
            binding.labelActivity.setText(resolveInfo.loadLabel(packageManager).toString());
        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            Intent intent = new Intent(Intent.ACTION_MAIN).setClassName(
                    activityInfo.applicationInfo.packageName, activityInfo.name).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private class Adapter extends RecyclerView.Adapter<Holder> {
        private final List<ResolveInfo> listOfActivities;

        public Adapter(List<ResolveInfo> listOfActivities) {
            this.listOfActivities = listOfActivities;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            LabelOfActivityBinding binding = DataBindingUtil.inflate(inflater,
                    R.layout.label_of_activity, viewGroup, false);

            return new Holder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int i) {
            holder.bind(listOfActivities.get(i));
        }

        @Override
        public int getItemCount() {
            return listOfActivities.size();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentLauncherBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_launcher, container, false);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(new Adapter(setupListOfActivitiesForAdapter()));

        return binding.getRoot();
    }
}
