# Comparison of Backup/Restore and Export/Import

**Backup/Restore**

- True system backup: backs up all regions, configurations and deployed jars 
- Supports incremental backups
- Data is written from all nodes in parallel
- Is a disk store based operation. It only works for persistent regions.
- Backups can only be restored into a cluster with the same number of data nodes.
- Cluster must be stopped during restore

**Export/Import**

- Not a full system backup but great tool for moving data between environments
- Can export individual regions
- Before GemFire 9, or when not using the `--parallel` option, all data is 
	sent to one node for writing to the file system.
- An export can be imported into a cluster with a different number of nodes (only if you do not use the `--parallel` option when exporting).
- Can be used with non-persistent regions
- Data can be imported while the cluster is running. However, when using PDX types you need to avoid inserting data during the import.
- You can restore data into a region with a different type (e.g. PARTITION vs REPLICATE) assuming you do not use the `--parallel` option.

