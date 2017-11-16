/**
 * Initialize the CRUD controller.
 * @$scope The scope to initialize.
 * @crudService The CRUD service used to request database.
 */
function initCrudController($scope, crudService) {
	$scope.lazy = false;
	
	$scope.paginationOptions = {
		pageNumber: 1,
		pageSize: 2,
		sort: null,
		filter: null
	};
	
	$scope.getPage = function() {
		if ($scope.lazy) {
			crudService.find($scope.paginationOptions, function(results) {
				$scope.gridOptions.data = results.results;
				$scope.gridOptions.totalItems = results.total;
			});
		} else {
			crudService.list(function(results) {
				$scope.gridOptions.data = results;
				$scope.gridOptions.totalItems = results.length;
			});
		}
	};
	
	$scope.gridOptions = {
		enableGridMenu: true,
		
		// Pagination
		paginationPageSizes: [2, 5, 10],
		paginationPageSize: $scope.paginationOptions.pageSize,
		// Lazy-loading
		useExternalPagination: $scope.lazy,
		onRegisterApi: function(gridApi) {
			// Pagination
			gridApi.pagination.on.paginationChanged($scope, function(newPage, pageSize) {
				$scope.paginationOptions.pageNumber = newPage;
				$scope.paginationOptions.pageSize = pageSize;
				$scope.getPage();
			});
			// Sorting
			gridApi.core.on.sortChanged($scope, function(grid, sortColumns) {
				if (sortColumns.length === 0)
					$scope.paginationOptions.sort = null;
				else
					$scope.paginationOptions.sort = {
						name: sortColumns[0].name,
						order: sortColumns[0].sort.direction
					};
				$scope.getPage();
			});
			// TODO Filtering
		},
		
		showGridFooter: true,
		gridFooterTemplate: '<button ng-click="grid.appScope.openCreateDialog()" type="button">' + 'Create' + '</div>', // TODO translate
	};
	
	$scope.getPage(); // initial load
	
	// Actions
	$scope.create = function() {
		crudService.create($scope.selectedValue);
	};
	$scope.update = function() {
		crudService.update($scope.selectedValue);
	};
	$scope.delete = function() {
		crudService.delete($scope.selectedValue);
	};
};