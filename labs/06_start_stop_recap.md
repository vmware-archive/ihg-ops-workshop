# Recap of Best Practices for Starting and Stopping 

- start cache servers concurrently, not sequentially
- do not stop a cache server if any buckets lack redundancy
- restart as rolling bounce, wait for redundancy as needed
- use “gfsh shutdown” to stop the whole cluster (not one by one)
- redundancy may not be restored after a failure (depends on settings)
- data will not automatically spread to new machines (use rebalance)

