package main

import (
	"errors"
	"fmt"
	"strconv"
	"encoding/json"
	"github.com/hyperledger/fabric/core/chaincode/shim"
)

// SimpleChaincode example simple Chaincode implementation:
type SimpleChaincode struct {
}

type AssetWrapper struct {
	ID     string `json:"id"`
	Status string `json:"status"`
	Asset struct {
		Donator struct {
			UserId    string `json:"user-id"`
			GivenName string `json:"given-name"`
			Telephone string `json:"telephone"`
			Image     string `json:"image"`
			Email     string `json:"email"`
		} `json:"donator"`
		Item struct {
			ProductId   string `json:"product-id"`
			Name        string `json:"name"`
			Image       string `json:"image"`
			CreatedOn   string `json:"created-on"`
			Quantity    string `json:"quantity"`
			Description string `json:"description"`
			Category    string `json:"category"`
			SubCategory string `json:"sub-category"`
		} `json:"item"`
	}
}

var itemIndexStr = "_itemindex"

type ItemListModel struct {
	ID          string `json:"ID"`
	Status      string `json:"status"`
	Title       string `json:"title"`
	Donator     string `json:"donatorName"`
	Category    string `json:"Category"`
	SubCategory string `json:"SubCategory"`
	Quantity    string `json:"quantity"`
}

// ============================================================================================================================
// Main
// ============================================================================================================================
func main() {
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}

// ============================================================================================================================
// Init - reset all the things
// ============================================================================================================================
func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface, function string, args []string) ([]byte, error) {
	var Aval int
	var err error

	if len(args) != 1 {
		return nil, errors.New("Incorrect number of arguments. Expecting 1")
	}

	// Initialize the chaincode
	Aval, err = strconv.Atoi(args[0])
	if err != nil {
		return nil, errors.New("Expecting integer value for asset holding")
	}

	// Write the state to the ledger
	err = stub.PutState("abc", []byte(strconv.Itoa(Aval)))
	if err != nil {
		return nil, err
	}

	var empty []string
	jsonAsBytes, _ := json.Marshal(empty)
	err = stub.PutState(itemIndexStr, jsonAsBytes)
	if err != nil {
		return nil, err
	}

	return nil, nil
}

// ============================================================================================================================
// Run - Our entry point for Invocations - [LEGACY] obc-peer 4/25/2016
// ============================================================================================================================
func (t *SimpleChaincode) Run(stub shim.ChaincodeStubInterface, function string, args []string) ([]byte, error) {
	fmt.Println("run is running " + function)
	return t.Invoke(stub, function, args)
}

// ============================================================================================================================
// Invoke - Our entry point for Invocations
// ============================================================================================================================
func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface, function string, args []string) ([]byte, error) {
	fmt.Println("invoke is running " + function)

	// Handle different functions
	if function == "init" {
		//initialize the chaincode state, used as reset
		return t.Init(stub, "init", args)
	} else if function == "init_item" {
		//create a new item
		return t.init_item(stub, args)
	} else if function == "update_item" {
		return t.update_item(stub, args)
	} else if function == "update_donator" {
		return t.update_donator(stub, args)
	} else if function == "verify_donation" {
		return t.verify_donation(stub, args)
	}
	fmt.Println("invoke did not find func: " + function)
	return nil, errors.New("Received unknown function invocation")
}

// ============================================================================================================================
// Query - Our entry point for Queries
// ============================================================================================================================
func (t *SimpleChaincode) Query(stub shim.ChaincodeStubInterface, function string, args []string) ([]byte, error) {
	fmt.Println("query is running " + function)

	// Handle different functions
	if function == "read" {
		//read a variable
		return t.read(stub, args)
	} else if function == "get_allitems_for_userId" {
		allItemsForUserId, err := t.get_allitems_for_userId(stub, args)
		if err != nil {
			fmt.Println("Error from get_items")
			return nil, err
		} else {
			allItemsBytes, err1 := json.Marshal(&allItemsForUserId)
			if err1 != nil {
				fmt.Println("Error marshalling allitems")
				return nil, err1
			}
			fmt.Println("all success, returning allitems")
			return allItemsBytes, nil
		}
	} else if function == "get_allitems" {
		allItems, err := t.get_allitems(stub)
		if err != nil {
			fmt.Println("Error from get_items")
			return nil, err
		} else {
			allItemsBytes, err1 := json.Marshal(&allItems)
			if err1 != nil {
				fmt.Println("Error marshalling allitems")
				return nil, err1
			}
			fmt.Println("all success, returning allitems")
			return allItemsBytes, nil
		}
	}
	fmt.Println("invoke did not find func: " + function)
	return nil, errors.New("Received unknown function invocation")
}

// ============================================================================================================================
// Read - read a variable from chaincode state
// ============================================================================================================================
func (t *SimpleChaincode) read(stub shim.ChaincodeStubInterface, args []string) ([]byte, error) {
	var name, jsonResp string
	var err error

	if len(args) != 1 {
		return nil, errors.New("Incorrect number of arguments. Expecting name of the var to query")
	}

	name = args[0]
	valAsbytes, err := stub.GetState(name) //get the var from chaincode state
	if err != nil {
		jsonResp = "{\"Error\":\"Failed to get state for " + name + "\"}"
		return nil, errors.New(jsonResp)
	}

	return valAsbytes, nil
}

// ============================================================================================================================
// Init Item - create a new item, store into chaincode state
// ============================================================================================================================
func (t *SimpleChaincode) init_item(stub shim.ChaincodeStubInterface, args []string) ([]byte, error) {
	var err error

	if len(args) != 8 {
		return nil, errors.New("Incorrect number of arguments. Expecting 4")
	}

	//input sanitation
	fmt.Println("- start init item")
	if len(args[0]) <= 0 {
		return nil, errors.New("1st argument must be a non-empty string")
	}
	if len(args[1]) <= 0 {
		return nil, errors.New("2nd argument must be a non-empty string")
	}

	id := args[0]
	productId := args[1]
	quantity := args[2]
	createdOn := args[3]
	itemname := args[4]
	donatorname := args[5]
	username := args[6]
	category := args[7]

	//check if item already exists
	itemAsBytes, err := stub.GetState(id)
	if err != nil {
		return nil, errors.New("Failed to get marble name")
	}
	res := AssetWrapper{}
	json.Unmarshal(itemAsBytes, &res)
	if res.ID == id {
		fmt.Println("This item arleady exists: " + id)
		fmt.Println(res);
		return nil, errors.New("This item already exists")
	}

	res.ID = id
	res.Asset.Item.ProductId = productId
	res.Asset.Item.Quantity = quantity
	res.Asset.Item.CreatedOn = createdOn
	res.Asset.Item.Name = itemname
	res.Asset.Donator.GivenName = donatorname
	res.Asset.Donator.UserId = username
	res.Asset.Item.Category = category
	// think about this
	res.Status = "NOT_DONATED"
	// and this issuedOn
	jsonAsBytes, _ := json.Marshal(res)
	err = stub.PutState(id, jsonAsBytes)
	if err != nil {
		return nil, err
	}

	itemAsBytes, err = stub.GetState(itemIndexStr)
	if err != nil {
		return nil, errors.New("Failed to get item index")
	}
	var itemIndex []string
	json.Unmarshal(itemAsBytes, &itemIndex)

	//append
	itemIndex = append(itemIndex, id)
	fmt.Println("! item index: ", itemIndex)
	jsonAsBytes, _ = json.Marshal(itemIndex)
	err = stub.PutState(itemIndexStr, jsonAsBytes)

	fmt.Println("- end init item")
	return nil, nil
}

func (t *SimpleChaincode) update_item(stub shim.ChaincodeStubInterface, args []string) ([]byte, error) {
	var err error
	var Aval int

	if len(args) != 7 {
		return nil, errors.New("Incorrect number of arguments. Expecting 6")
	}

	//input sanitation
	fmt.Println("- start updating item")

	id := args[0]
	name := args[1]
	category := args[2]
	subcategory := args[3]
	image := args[4]
	description := args[5]
	quantity := args[6]

	//check if item already exists
	itemAsBytes, err := stub.GetState(id)
	if err != nil {
		return nil, errors.New("Failed to get item")
	}
	res := AssetWrapper{}
	json.Unmarshal(itemAsBytes, &res)
	if res.ID != id {
		fmt.Println("This item not exists: " + id)
		fmt.Println(res);
		return nil, errors.New("This item not exists")
	}

	res.Asset.Item.Name = name
	res.Asset.Item.Category = category
	res.Asset.Item.Description = description
	res.Asset.Item.SubCategory = subcategory

	if len(image) > 0 {
		res.Asset.Item.Image = image
	}

	// Initialize the chaincode
	Aval, err = strconv.Atoi(args[6])
	if err != nil {
		return nil, errors.New("Expecting integer value for asset holding")
	}
	if Aval <= 0 {
		return nil, errors.New("Cannot insert negative or zero quantity")
	}
	res.Asset.Item.Quantity = quantity;
	jsonAsBytes, _ := json.Marshal(res) //save new index
	err = stub.PutState(id, jsonAsBytes)
	if err != nil {
		return nil, err
	}

	//get the item index
	itemAsBytes, err = stub.GetState(id)
	if err != nil {
		return nil, errors.New("Failed to get item index, item is not stored")
	}
	//store name of marble

	fmt.Println("- end updating item")
	return nil, nil
}

func (t *SimpleChaincode) update_donator(stub shim.ChaincodeStubInterface, args []string) ([]byte, error) {
	var err error

	if len(args) != 6 {
		return nil, errors.New("Incorrect number of arguments. Expecting 4")
	}

	//input sanitation
	fmt.Println("- start updating reciever")

	id := args[0]
	telephone := args[1]
	email := args[2]
	name := args[3]
	image := args[4]
	userId := args[5]

	//check if item already exists
	itemAsBytes, err := stub.GetState(id)
	if err != nil {
		return nil, errors.New("Failed to get item name")
	}
	res := AssetWrapper{}
	json.Unmarshal(itemAsBytes, &res)
	if res.ID != id {
		fmt.Println("This item not exists: " + id)
		fmt.Println(res);
		return nil, errors.New("This item not exists")
	}

	res.Asset.Donator.UserId = userId // maybe this have to be changed
	res.Asset.Donator.GivenName = name
	res.Asset.Donator.Email = email
	res.Asset.Donator.Telephone = telephone

	if len(image) > 0 {
		res.Asset.Donator.Image = image
	}

	jsonAsBytes, _ := json.Marshal(res) //save new index
	err = stub.PutState(id, jsonAsBytes)
	if err != nil {
		return nil, err
	}

	//get the item index
	itemAsBytes, err = stub.GetState(id)
	if err != nil {
		return nil, errors.New("Failed to get item index, item is not stored")
	}
	//store name of marble

	fmt.Println("- end updating issuer")
	return nil, nil
}

func (t *SimpleChaincode) verify_donation(stub shim.ChaincodeStubInterface, args []string) ([]byte, error) {
	var err error
	var quantity_for_transver int
	var item_quantity int

	if len(args) != 2 {
		return nil, errors.New("Incorrect number of arguments. Expecting 2")
	}

	fmt.Println("- start verifying item")

	id := args[0]
	quantity_for_transver, err = strconv.Atoi(args[1])
	if err != nil {
		return nil, errors.New("Expecting integer value for asset holding")
	}
	//check if item already exists
	itemAsBytes, err := stub.GetState(id)
	if err != nil {
		return nil, errors.New("Failed to get item name")
	}
	res := AssetWrapper{}
	json.Unmarshal(itemAsBytes, &res)
	if res.ID != id {
		fmt.Println("This item not exists: " + id)
		fmt.Println(res);
		return nil, errors.New("This item not exists")
	}
	item_quantity, err = strconv.Atoi(res.Asset.Item.Quantity)
	if err != nil {
		return nil, errors.New("Problem with parsing the value from the item")
	}

	if (quantity_for_transver > item_quantity) {
		return nil, errors.New("This item hasn't enough guantity as you requested")
	} else {
		item_quantity = item_quantity - quantity_for_transver
		res.Asset.Item.Quantity = strconv.Itoa(item_quantity)
	}
	if (item_quantity == 0) {
		res.Status = "DONATED"
	}

	jsonAsBytes, _ := json.Marshal(res) //save new index
	err = stub.PutState(id, jsonAsBytes)
	if err != nil {
		return nil, err
	}

	//get the item index
	itemAsBytes, err = stub.GetState(id)
	if err != nil {
		return nil, errors.New("Failed to get item index, item is not stored")
	}

	fmt.Println("- end verifying item")
	return nil, nil
}

func (t *SimpleChaincode) get_allitems_for_userId(stub shim.ChaincodeStubInterface, args []string) ([]ItemListModel, error) {

	if len(args) <= 0 {
		return nil, errors.New("1st argument must be a non-empty string")
	}

	var allItems [] ItemListModel
	// Get list of all the keys
	keysBytes, err := stub.GetState(itemIndexStr)
	if err != nil {
		fmt.Println("Error retrieving item indexes")
		return nil, errors.New("Error retrieving item indexes")
	}
	var ids []string
	err = json.Unmarshal(keysBytes, &ids)
	if err != nil {
		fmt.Println("Error unmarshalling keybytes")
		return nil, errors.New("Error unmarshalling keybytes")
	}

	for _, value := range ids {
		itemBytes, err := stub.GetState(value)
		var asset AssetWrapper
		err = json.Unmarshal(itemBytes, &asset)
		if err != nil {
			fmt.Println("Error retrieving item " + value)
			return nil, errors.New("Error retrieving item " + value)
		}
		if asset.Asset.Donator.UserId == args[0] {
			newItem := ItemListModel{}
			newItem.Donator = asset.Asset.Donator.GivenName
			newItem.Status = asset.Status
			newItem.ID = asset.ID
			newItem.Title = asset.Asset.Item.Name
			newItem.Category = asset.Asset.Item.Category
			newItem.SubCategory = asset.Asset.Item.SubCategory
			newItem.Quantity = asset.Asset.Item.Quantity
			fmt.Println("Appending item" + value)
			allItems = append(allItems, newItem)
		}
	}
	return allItems, nil
}

func (t *SimpleChaincode) get_allitems(stub shim.ChaincodeStubInterface) ([]ItemListModel, error) {

	var allItems [] ItemListModel
	// Get list of all the keys
	keysBytes, err := stub.GetState(itemIndexStr)
	if err != nil {
		fmt.Println("Error retrieving item indexes")
		return nil, errors.New("Error retrieving item indexes")
	}
	var ids []string
	err = json.Unmarshal(keysBytes, &ids)
	if err != nil {
		fmt.Println("Error unmarshalling keybytes")
		return nil, errors.New("Error unmarshalling keybytes")
	}

	for _, value := range ids {
		itemBytes, err := stub.GetState(value)
		var asset AssetWrapper
		err = json.Unmarshal(itemBytes, &asset)
		if err != nil {
			fmt.Println("Error retrieving item " + value)
			return nil, errors.New("Error retrieving item " + value)
		}
		newItem := ItemListModel{}
		newItem.Donator = asset.Asset.Donator.GivenName
		newItem.Status = asset.Status
		newItem.ID = asset.ID
		newItem.Title = asset.Asset.Item.Name
		newItem.Category = asset.Asset.Item.Category
		newItem.SubCategory = asset.Asset.Item.SubCategory
		newItem.Quantity = asset.Asset.Item.Quantity
		fmt.Println("Appending item" + value)
		allItems = append(allItems, newItem)
	}
	return allItems, nil
}
